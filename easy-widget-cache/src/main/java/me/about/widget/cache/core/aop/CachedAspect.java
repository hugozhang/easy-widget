package me.about.widget.cache.core.aop;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import me.about.widget.cache.annotation.Cached;
import me.about.widget.cache.annotation.FieldName;
import me.about.widget.cache.core.CacheManager;
import me.about.widget.cache.entity.*;
import me.about.widget.cache.enums.CacheType;
import me.about.widget.cache.util.Constants;
import me.about.widget.cache.util.SpELParser;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.support.NullValue;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 多级缓存AOP
 *
 * @author: hugo.zxh
 * @date: 2022/06/14 15:34
 * @description:
 */
@Slf4j
@Aspect
@Component
public class CachedAspect {

    @Resource
    private CacheManager cacheManager;

    @Pointcut("@annotation(me.about.widget.cache.annotation.Cached)")
    public void cached() {
    }

    @Around("cached()")
    public Object doMultiLevelCache(ProceedingJoinPoint joinPoint) throws Throwable {

        // 调用方信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Class<?> returnType = method.getReturnType();

        CacheInvokeContext invokeContext = new CacheInvokeContext();
        invokeContext.setInvoker(joinPoint::proceed);
        invokeContext.setMethod(method);
        invokeContext.setArgs(args);
        invokeContext.setReturnType(returnType);

        // 注解信息
        Cached cached = method.getAnnotation(Cached.class);

        // 方法注解信息
        String keyPrefix = cached.keyPrefix();
        String keyExpr = cached.key();

        // 对象过期
        long expire = cached.expire();
        TimeUnit timeUnit = cached.timeUnit();
        // 空值缓存过期
        long emptyExpire = cached.emptyExpire();
        TimeUnit emptyTimeUnit = cached.emptyTimeUnit();

        if (StringUtils.isBlank(keyExpr)) {
            log.error("cache key is null or empty.");
            return invokeOrigin(invokeContext);
        }

//        if (args.length == 0) {
//            return invokeOrigin(invokeContext);
//        }

        Object keyValue = SpELParser.evalKey(keyExpr,method,args);
        if (keyValue == null) {
            log.error("cache key is null.");
            return invokeOrigin(invokeContext);
        }
        //解析key
        String cacheKey = buildCacheKey(keyPrefix, keyValue);

        CacheInvokeConfig cacheInvokeConfig = new CacheInvokeConfig();
        cacheInvokeConfig.setCacheKey(cacheKey);
        cacheInvokeConfig.setExpire(expire);
        cacheInvokeConfig.setTimeUnit(timeUnit);
        cacheInvokeConfig.setEmptyExpire(emptyExpire);
        cacheInvokeConfig.setEmptyTimeUnit(emptyTimeUnit);
        invokeContext.setCacheInvokeConfig(cacheInvokeConfig);

        //是不是清缓存
        if (cached.type() == CacheType.REMOVE) {
            cacheManager.remove(cacheKey);
            return invokeOrigin(invokeContext);
        } else if (cached.type() == CacheType.GET) {
            if (returnType == void.class) {
                // void
                log.error("[Cache] cache op is get,but cache returnType is void.");
                return invokeOrigin(invokeContext);
            } else if (returnType == List.class) {
                //一对多查询
                log.debug("[Cache] in mode : one to many -> " + methodSignature);
                return do1ToNCache(invokeContext);
            } else {
                // 一对一
                log.debug("[Cache] in mode : one to one -> " + methodSignature);
                return do1To1Cache(invokeContext);
            }
        } else if (cached.type() == CacheType.IN_QUERY) {
            //多对多查询
            log.debug("[Cache] in mode : many to many -> " + methodSignature);
            return doManyToManyCache(invokeContext);
        }
        return invokeOrigin(invokeContext);
    }

    private static String buildCacheKey(String keyPrefix, Object keyValue) {
        return (StringUtils.isBlank(keyPrefix)) ? (String) keyValue :
                (keyPrefix.lastIndexOf(Constants.JOIN_ON) != -1 ? keyPrefix : keyPrefix + Constants.JOIN_ON) + keyValue;
    }


    private static Object invokeOrigin(CacheInvokeContext context) throws Throwable {
        return context.getInvoker().invoke(context.getArgs());
    }


    private boolean isInQueryMode(MethodParameter methodParameter,int multiKeysIndex) {
        return methodParameter.getParameterValue() instanceof List
                && methodParameter.getParameterIndex().compareTo(multiKeysIndex) == 0;
    }

    /**
     * 多对多关系
     */
    private Object doManyToManyCache(CacheInvokeContext invokeContext) throws Throwable {
        CacheInvokeConfig invokeConfig = invokeContext.getCacheInvokeConfig();
        //1、如果最后一个参数是list，说明db查询是带in条件的
        List<Object> result = new ArrayList<>();
        List<Object> needQuery = new ArrayList<>();
//        InQueryMode inQueryMode = getFieldNames(invokeContext.getMethod());
//        MethodParameter[] methodParameters = buildMethodParameter(invokeContext.getMethod(), invokeContext.getArgs());

        ManyToManyParam manyToManyParam = buildManyToManyParam(invokeContext.getMethod(), invokeContext.getArgs());
        invokeConfig.setManyToManyParam(manyToManyParam);

        // 组装key 要考虑列名
        List<String> keys = ((List<?>)manyToManyParam.getArgValue())
                .stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());

        //所有查询key   之前是一行一行put，现在是批量操作
        List<Object> cacheValues = cacheManager.getAll(keys,invokeConfig);
        for (int i = 0,len = cacheValues.size() ; i < len ; i++) {
            Object value = cacheValues.get(i);
            if (value != null) {
                //缓存的空值不返回到结果集
                if (value != NullValue.INSTANCE) {
                    result.add(value);
                }
            } else {
                //不在缓存的需要查询一次db
                needQuery.add(keys.get(i));
            }
        }

        if (needQuery.isEmpty()) {
            return result;
        }
        Map<String,Set<Object>> keyValues = new HashMap<>();
        //2、没有命中缓存中，需要把对应的参数组装查询db，查到的数据就放进缓存
        List<Object> existDb = new ArrayList<>();
        //3、更新原方法参数，要先找到参数索引，用needQuery重新覆盖
        invokeContext.getArgs()[manyToManyParam.getArgIndex()] = needQuery;
        Object proceed = invokeOrigin(invokeContext);
        if (proceed instanceof List) {
            List<?> list = (List<?>)proceed;
            for (Object o : list) {
                //多对多  比如in查询返回list 怎么缓存
                //key是什么 value 是什么
                //找到对象中哪个列的值和它匹配的 以它生成key
                Arrays.stream(manyToManyParam.getFieldNames()).forEach(e -> {
                    Object fieldValue = getFieldValue(o, e);
                    if (needQuery.contains(fieldValue)) {
                        existDb.add(fieldValue);
                        result.add(o);
                        String cacheKey = fieldValue.toString();
                        keyValues.computeIfAbsent(cacheKey, k -> Sets.newHashSet()).add(o);
                    }
                });
            }
        }
        //4、比较needQuery与existDb的差集  不在db里面的内容是否需要做空缓存
        needQuery.removeAll(existDb);
        if (invokeConfig.getEmptyExpire() != Constants.ALLOW_NULL_VALUE) {
            for (Object o : needQuery) {
                String cacheKey =  o.toString();
                keyValues.computeIfAbsent(cacheKey, k -> Sets.newHashSet()).add(NullValue.INSTANCE);
            }
        }
        //5、批量更新进缓存 [[]] 二维结构转一维结构
        if (!keyValues.isEmpty()) {
            Map<String, Object> convertedMap = new HashMap<>();
            for (Map.Entry<String, Set<Object>> entry : keyValues.entrySet()) {
                // 如果只有一个值，则是一对一关系,否则一对多
                List<Object> arrayList = new ArrayList<>(entry.getValue());
                convertedMap.put(entry.getKey(), arrayList.size() == 1 ? arrayList.get(0) : arrayList);
            }
            cacheManager.putAll(convertedMap,invokeConfig);
        }
        return result;
    }

    private void doUpdate(Object cacheKey, Object value, Long expire, TimeUnit timeUnit) {
        cacheManager.put(cacheKey, value, expire, timeUnit);
    }

    /**
     * 一对一关系
     */
    private Object do1To1Cache(CacheInvokeContext invokeContext) throws Throwable {
        CacheInvokeConfig invokeConfig = invokeContext.getCacheInvokeConfig();
        Object cacheObject = cacheManager.get(invokeConfig.getCacheKey());
        if (cacheObject != null) {
            return cacheObject == NullValue.INSTANCE ? null : cacheObject;
        }
        Object proceed = invokeOrigin(invokeContext);
        if (proceed == null && invokeConfig.getEmptyExpire() != Constants.ALLOW_NULL_VALUE) {
            doUpdate(invokeConfig.getCacheKey(),NullValue.INSTANCE,invokeConfig.getEmptyExpire(),invokeConfig.getEmptyTimeUnit());
        } else {
            doUpdate(invokeConfig.getCacheKey(),proceed,invokeConfig.getExpire(),invokeConfig.getTimeUnit());
        }
        return proceed;
    }

    /**
     * 一对多关系
     */
    private Object do1ToNCache(CacheInvokeContext invokeContext) throws Throwable {
        CacheInvokeConfig invokeConfig = invokeContext.getCacheInvokeConfig();
        Object cacheObject = cacheManager.get(invokeConfig.getCacheKey());
        if (cacheObject != null) {
            return cacheObject;
        }
        Object proceed = invokeOrigin(invokeContext);
        if (proceed instanceof List) {
            List<?> result = (List<?>) proceed;
            if (invokeConfig.getEmptyExpire() != Constants.ALLOW_NULL_VALUE) {
                if (result.isEmpty()) {
                    doUpdate(invokeConfig.getCacheKey(), Constants.EMPTY_LIST,invokeConfig.getEmptyExpire(),invokeConfig.getTimeUnit());
                } else {
                    doUpdate(invokeConfig.getCacheKey(),proceed,invokeConfig.getExpire(),invokeConfig.getTimeUnit());
                }
            }
        }
        return proceed;
    }


    private ManyToManyParam buildManyToManyParam(Method method,Object [] args)  {
        ManyToManyParam manyToManyParam = new ManyToManyParam();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            FieldName mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(parameters[i], FieldName.class);
            if (mergedAnnotation != null) {
                manyToManyParam.setFieldNames(mergedAnnotation.value());
                manyToManyParam.setArgIndex(i);
                manyToManyParam.setArgValue(args[i]);
            }
        }
        return manyToManyParam;
    }

    /**
     * in查询数值对应的参数名称
     */
    private InQueryMode getFieldNames(Method method) {
        InQueryMode inQueryMode = new InQueryMode();
        List<String> fieldNames = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation ann: paramAnnotations[i]) {
                if(!(ann instanceof FieldName)) {
                    continue;
                }
                if (paramTypes[i] != List.class) {
                    throw new RuntimeException("@FieldName 标注的参数类型必须是List类型");
                }
                String fieldName = null;  //((FieldName) ann).value();
                String parameterName = parameters[i].getName();
                fieldNames.add(fieldName);
                inQueryMode.setFieldName(fieldName);
                inQueryMode.setParameterName(parameterName);
                inQueryMode.setParameterType(paramTypes[i]);
                inQueryMode.setParameterIndex(i);
            }
        }
        if (fieldNames.size() != 1) {
            throw new RuntimeException("in查询模式 => @FieldName 标注的参数只支持一个");
        }
        return inQueryMode;
    }


   private MethodParameter[] buildMethodParameter(Method method, Object [] args) {
       MethodParameter[] methodParameters = new MethodParameter[args.length];
       LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
       String[] paraNameArr = Optional.ofNullable(u.getParameterNames(method)).orElse(new String[0]);
       for(int i = 0; i < paraNameArr.length; i++) {
           MethodParameter methodParameter = new MethodParameter();
           methodParameter.setParameterName(paraNameArr[i]);
           methodParameter.setParameterValue(args[i]);
           methodParameter.setParameterIndex(i);
           methodParameters[i] = methodParameter;
       }
       return methodParameters;
   }

    public Object getFieldValue(Object object, String fieldName) {
        Field field = ReflectionUtils.findField(object.getClass(), fieldName);
        if (field == null) {
            return new NoSuchFieldException(object.getClass() + "没有字段" + fieldName);
        }
        field.setAccessible(true);
        return ReflectionUtils.getField(field,object);
    }
}
