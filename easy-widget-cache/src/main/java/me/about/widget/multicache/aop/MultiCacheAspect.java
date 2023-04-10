package me.about.widget.multicache.aop;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.multicache.annotation.FieldName;
import me.about.widget.multicache.annotation.MultiLevelCache;
import me.about.widget.multicache.annotation.MultiLevelTypeEnum;
import me.about.widget.multicache.cache.CacheManager;
import me.about.widget.multicache.util.Constants;
import me.about.widget.multicache.entity.InQueryMode;
import me.about.widget.multicache.entity.MethodParameter;
import me.about.widget.multicache.util.SpelParser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.support.NullValue;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
public class MultiCacheAspect {

    @Resource
    private CacheManager cacheManager;

    @Pointcut("@annotation(me.about.widget.multicache.annotation.MultiLevelCache)")
    public void multiLevelCache() {
    }

    @Around("multiLevelCache()")
    public Object doMultiLevelCache(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        MultiLevelCache multiLevelCache = method.getAnnotation(MultiLevelCache.class);

        //方法注解信息
        String keyPrefix = multiLevelCache.keyPrefix();
        String key = multiLevelCache.key();

        //普通对象过期
        long expire = multiLevelCache.expire();
        TimeUnit timeUnit = multiLevelCache.timeUnit();
        //空值缓存过期
        long emptyExpire = multiLevelCache.emptyExpire();
        TimeUnit emptyTimeUnit = multiLevelCache.emptyTimeUnit();

        //方法签名信息
        Object[] args = joinPoint.getArgs();
        Class<?> returnType = method.getReturnType();

        if (key == null || key.trim().length() == 0) {
            log.error("【分级缓存】cache key is null.");
            return joinPoint.proceed(args);
        }

        if (args.length == 0) {
            return joinPoint.proceed(args);
        }

        String spel = SpelParser.parseKey(key,method,args);
        //解析key
        String parseKey = (keyPrefix == null || keyPrefix.trim().length() == 0) ? spel : keyPrefix + Constants.JOIN_ON + spel;
        //是不是清缓存
        if (multiLevelCache.type() == MultiLevelTypeEnum.EVICT) {
            cacheManager.evict(parseKey);
            return joinPoint.proceed(args);
        }

        if (multiLevelCache.type() == MultiLevelTypeEnum.GET) {
            return cacheManager.get(parseKey);
        }

        //分两种情况
        //1、针对db中in查询的情况 多对多关系
        if (multiLevelCache.type() == MultiLevelTypeEnum.IN_QUERY) {
            log.info("【分级缓存】in查询模式：多对多关系：" + methodSignature);
            return doManyToManyCache(joinPoint, method,expire,timeUnit, emptyExpire, emptyTimeUnit, args, parseKey);
        }
        //2、针对db中非in查询的情况
        // 进入一对一或一对多逻辑
        return returnType == List.class ? do1ToNCache(joinPoint,parseKey,expire,timeUnit,emptyExpire,emptyTimeUnit) : do1To1Cache(joinPoint,parseKey,expire,timeUnit,emptyExpire,emptyTimeUnit);
    }

    /**
     * 多对多关系
     * @param joinPoint
     * @param method
     * @param emptyExpire
     * @param emptyTimeUnit
     * @param args
     * @param parseKey
     * @return
     * @throws Throwable
     */
    private Object doManyToManyCache(ProceedingJoinPoint joinPoint, Method method,
                                     long expire, TimeUnit timeUnit,
                                     long emptyExpire, TimeUnit emptyTimeUnit,
                                     Object[] args, String parseKey) throws Throwable {
        InQueryMode inQueryMode = getFieldNames(method);
        //1、如果最后一个参数是list，说明db查询是带in条件的
        List<Object> result = new ArrayList();
        List<Object> needQuery = new ArrayList<>();
        MethodParameter[] methodParameters = buildMethodParameter(method, args);
        Arrays.stream(methodParameters).forEach(e -> {
            if (e.getParameterValue() instanceof List
                    && e.getParameterIndex().compareTo(inQueryMode.getParameterIndex()) == 0) {
                List paramList = (List)e.getParameterValue();
                for (Object param : paramList) {
                    //作为缓存key去找
                    String cacheKey = parseKey + Constants.JOIN_ON + param.toString();
                    Object cacheObject = cacheManager.get(cacheKey);
                    if (cacheObject != null) {
                        //空值不返回到结果集
                        if (cacheObject != NullValue.INSTANCE) {
                            result.add(cacheObject);
                        }
                    } else {
                        //不在缓存的要查询一次db
                        needQuery.add(param);
                    }
                }
            }
        });

        if (needQuery.isEmpty()) {
            return result;
        }
        //2、没有命中缓存中，需要把对应的参数组装查询db，查到的数据就放进缓存
        List existDb = new ArrayList();
        // 更新原来参数所在位置的参数  确定list参数的位置 然后重新覆盖
        args[inQueryMode.getParameterIndex()] = needQuery;
        Object proceed = joinPoint.proceed(args);
        if (proceed instanceof List) {
            List list = (List)proceed;
            for (Object o : list) {
                Object v = getFieldValue(o,inQueryMode.getFieldName());
                existDb.add(v);
                result.add(o);
                String cacheKey = parseKey + Constants.JOIN_ON + v.toString();
                cacheManager.put(cacheKey, o, expire, timeUnit);
            }
        }
        //3、比较needQuery与existDb的差集  不在db里面的内容是否需要做空缓存
        needQuery.removeAll(existDb);
        if (emptyExpire != Constants.ALLOW_NULL_VALUE) {
            for (Object o : needQuery) {
                String cacheKey = parseKey + Constants.JOIN_ON + o.toString();
                cacheManager.put(cacheKey, NullValue.INSTANCE, emptyExpire, emptyTimeUnit);
            }
        }
        return result;
    }

    /**
     * 一对一关系
     * @param joinPoint
     * @param key
     * @param expire
     * @param timeUnit
     * @param emptyExpire
     * @param emptyTimeUnit
     * @return
     * @throws Throwable
     */
    private Object do1To1Cache(ProceedingJoinPoint joinPoint,String key,
                               long expire,TimeUnit timeUnit,
                               long emptyExpire,TimeUnit emptyTimeUnit) throws Throwable {
        Object cacheObject = cacheManager.get(key);
        if (cacheObject != null) {
            return cacheObject == NullValue.INSTANCE ? null : cacheObject;
        }
        Object proceed = joinPoint.proceed();
        if (proceed == null && emptyExpire != Constants.ALLOW_NULL_VALUE) {
            cacheManager.put(key,NullValue.INSTANCE,emptyExpire,emptyTimeUnit);
        } else {
            cacheManager.put(key,proceed,expire,timeUnit);
        }
        return proceed;
    }

    /**
     * 一对多关系
     * @param joinPoint
     * @param key
     * @param expire
     * @param timeUnit
     * @param emptyExpire
     * @param emptyTimeUnit
     * @return
     * @throws Throwable
     */
    private Object do1ToNCache(ProceedingJoinPoint joinPoint, String key,
                               long expire, TimeUnit timeUnit,
                               long emptyExpire, TimeUnit emptyTimeUnit) throws Throwable {
        List<Object> cacheList = cacheManager.get(key);
        if (cacheList != null) {
            return cacheList;
        }
        Object proceed = joinPoint.proceed();
        if (proceed instanceof List) {
            List<?> result = (List<?>) proceed;
            if (result.isEmpty() && emptyExpire != Constants.ALLOW_NULL_VALUE) {
                cacheManager.put(key, Constants.EMPTY_LIST,emptyExpire,emptyTimeUnit);
            } else {
                cacheManager.put(key,proceed,expire,timeUnit);
            }
        }
        return proceed;
    }

    /**
     * in查询数值对应的参数名称
     *
     * @param method
     * @return
     */
    private InQueryMode getFieldNames(Method method) {
        InQueryMode inQueryMode = new InQueryMode();
        List<String> fieldNames = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Class[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation a: paramAnnotations[i]) {
                if (a instanceof FieldName) {
                    String fieldName = ((FieldName) a).value();
                    String parameterName = parameters[i].getName();
                    fieldNames.add(fieldName);
                    if (paramTypes[i] != List.class) {
                        throw new RuntimeException("@FieldName 标注的参数类型必须是List类型");
                    }
                    inQueryMode.setFieldName(fieldName);
                    inQueryMode.setParameterName(parameterName);
                    inQueryMode.setParameterType(paramTypes[i]);
                    inQueryMode.setParameterIndex(i);
                }
            }
        }
        if (fieldNames.size() != 1) {
            throw new RuntimeException("in查询模式@FieldName标注的参数只支持一个");
        }
        return inQueryMode;
    }


   private MethodParameter[] buildMethodParameter(Method method, Object [] args) {
       MethodParameter[] methodParameters = new MethodParameter[args.length];
       LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
       String[] paraNameArr = u.getParameterNames(method);
       for(int i = 0; i < paraNameArr.length; i++) {
           MethodParameter methodParameter = new MethodParameter();
           methodParameter.setParameterName(paraNameArr[i]);
           methodParameter.setParameterValue(args[i]);
           methodParameter.setParameterIndex(i);
           methodParameters[i] = methodParameter;
       }
       return methodParameters;
   }

    public Field getDeclaredField(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        for(; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                log.debug(clazz + "，缺失字段：" + fieldName);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
        return null;
    }

    public Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch(Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }
}
