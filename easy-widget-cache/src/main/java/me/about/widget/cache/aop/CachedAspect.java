package me.about.widget.cache.aop;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.cache.annotation.FieldName;
import me.about.widget.cache.annotation.Cached;
import me.about.widget.cache.core.CacheManager;
import me.about.widget.cache.core.CacheOp;
import me.about.widget.cache.entity.InQueryMode;
import me.about.widget.cache.entity.MethodParameter;
import me.about.widget.cache.util.Constants;
import me.about.widget.cache.util.SpELParser;
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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
public class CachedAspect {

    @Resource
    private CacheManager cacheManager;

    @Pointcut("@annotation(me.about.widget.cache.annotation.Cached)")
    public void cached() {
    }


    private static Iterable toIterable(Object obj) {
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                return Arrays.asList((Object[]) obj);
            } else {
                List list = new ArrayList();
                int len = Array.getLength(obj);
                for (int i = 0; i < len; i++) {
                    list.add(Array.get(obj, i));
                }
                return list;
            }
        } else if (obj instanceof Iterable) {
            return (Iterable) obj;
        } else {
            return null;
        }
    }

    @Around("cached()")
    public Object doMultiLevelCache(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Cached cached = method.getAnnotation(Cached.class);

        //方法注解信息
        String name = cached.name();
        String keyScript = cached.key();

        //对象过期
        long expire = cached.expire();
        TimeUnit timeUnit = cached.timeUnit();
        //空值缓存过期
        long emptyExpire = cached.emptyExpire();
        TimeUnit emptyTimeUnit = cached.emptyTimeUnit();

        //方法签名信息
        Object[] args = joinPoint.getArgs();
        Class<?> returnType = method.getReturnType();

        if (keyScript == null || keyScript.trim().isEmpty()) {
            log.error("【分级缓存】cache keyScript is null.");
            return joinPoint.proceed(args);
        }

        if (args.length == 0) {
            return joinPoint.proceed(args);
        }

        Object key = SpELParser.evalKey(keyScript,method,args);
        if (key == null) {
            log.error("【分级缓存】cache key is null.");
            return joinPoint.proceed(args);
        }
        //解析key
        Object parseKey = (name == null || name.trim().isEmpty()) ? key : name + Constants.JOIN_ON + key;
        //是不是清缓存
        if (cached.cacheOp() == CacheOp.REMOVE) {
            cacheManager.remove(parseKey);
            return joinPoint.proceed(args);
        } else if (cached.cacheOp() == CacheOp.GET) {
            return do1To1Cache(joinPoint,parseKey,expire,timeUnit,emptyExpire,emptyTimeUnit);
        } else if (cached.cacheOp() == CacheOp.IN_QUERY) {
            //分两种情况
            //1、多对多查询  比如in查询，返回List
            log.info("【分级缓存】in查询模式：多对多关系：" + methodSignature);
            return doManyToManyCache(joinPoint, method,expire,timeUnit, emptyExpire, emptyTimeUnit, args, parseKey);
        }
        if (returnType == List.class) {
            //2、一对多查询  一对一查询
            return do1ToNCache(joinPoint,parseKey,expire,timeUnit,emptyExpire,emptyTimeUnit);
        }
        return joinPoint.proceed(args);
    }


    private boolean isInQueryMode(MethodParameter methodParameter,int mutilKeysIndex) {
        return methodParameter.getParameterValue() instanceof List
                && methodParameter.getParameterIndex().compareTo(mutilKeysIndex) == 0;
    }

    /**
     * 多对多关系
     * @param joinPoint
     * @param method
     * @param emptyExpire
     * @param emptyTimeUnit
     * @param args
     * @param key
     * @return
     * @throws Throwable
     */
    private Object doManyToManyCache(ProceedingJoinPoint joinPoint, Method method,
                                     long expire, TimeUnit timeUnit,
                                     long emptyExpire, TimeUnit emptyTimeUnit,
                                     Object[] args, Object key) throws Throwable {

        InQueryMode inQueryMode = getFieldNames(method);
        //1、如果最后一个参数是list，说明db查询是带in条件的
        List<Object> result = new ArrayList<>();
        List<Object> needQuery = new ArrayList<>();
        MethodParameter[] methodParameters = buildMethodParameter(method, args);
        Arrays.stream(methodParameters)
                .filter(e -> isInQueryMode(e, inQueryMode.getParameterIndex()))
                .forEach(e -> {
                    List paramList = (List)e.getParameterValue();
                    for (Object param : paramList) {
                        //作为缓存key去找
                        String cacheKey = key + Constants.JOIN_ON + param.toString();
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
                });

        if (needQuery.isEmpty()) {
            return result;
        }
        //2、没有命中缓存中，需要把对应的参数组装查询db，查到的数据就放进缓存
        List existDb = new ArrayList<>();
        //3、更新原方法参数，要先找到参数索引，用needQuery重新覆盖
        args[inQueryMode.getParameterIndex()] = needQuery;
        Object proceed = joinPoint.proceed(args);
        if (proceed instanceof List) {
            List list = (List)proceed;
            for (Object o : list) {
                Object v = getFieldValue(o,inQueryMode.getFieldName());
                existDb.add(v);
                result.add(o);
                String cacheKey = key + Constants.JOIN_ON + o;
                doUpdate(cacheKey, o, expire, timeUnit);
            }
        }
        //4、比较needQuery与existDb的差集  不在db里面的内容是否需要做空缓存
        needQuery.removeAll(existDb);
        if (emptyExpire != Constants.ALLOW_NULL_VALUE) {
            for (Object o : needQuery) {
                String cacheKey = key + Constants.JOIN_ON + o;
                doUpdate(cacheKey, NullValue.INSTANCE, emptyExpire, emptyTimeUnit);
            }
        }
        return result;
    }

    private void doUpdate(Object cacheKey, Object value, Long expire, TimeUnit timeUnit) {
        cacheManager.put(cacheKey, value, expire, timeUnit);
    }

    /**
     * 一对一关系
     * @param joinPoint
     * @param key
     * @param expire
     * @param timeUnit
     * @param emptyExpire
     * @param emptyTimeUnit
     * @return 缓存对象
     * @throws Throwable
     */
    private Object do1To1Cache(ProceedingJoinPoint joinPoint,Object key,
                               long expire,TimeUnit timeUnit,
                               long emptyExpire,TimeUnit emptyTimeUnit) throws Throwable {
        Object cacheObject = cacheManager.get(key);
        if (cacheObject != null) {
            return cacheObject == NullValue.INSTANCE ? null : cacheObject;
        }
        Object proceed = joinPoint.proceed();
        if (proceed == null && emptyExpire != Constants.ALLOW_NULL_VALUE) {
            doUpdate(key,NullValue.INSTANCE,emptyExpire,emptyTimeUnit);
        } else {
            doUpdate(key,proceed,expire,timeUnit);
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
    private Object do1ToNCache(ProceedingJoinPoint joinPoint, Object key,
                               long expire, TimeUnit timeUnit,
                               long emptyExpire, TimeUnit emptyTimeUnit) throws Throwable {
        Object cacheObject = cacheManager.get(key);
        if (cacheObject != null) {
            return cacheObject;
        }
        Object proceed = joinPoint.proceed();
        if (proceed instanceof List) {
            List<?> result = (List<?>) proceed;
            if (result.isEmpty() && emptyExpire != Constants.ALLOW_NULL_VALUE) {
                doUpdate(key, Constants.EMPTY_LIST,emptyExpire,emptyTimeUnit);
            } else {
                doUpdate(key,proceed,expire,timeUnit);
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
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation ann: paramAnnotations[i]) {
                if(!(ann instanceof FieldName)) {
                    continue;
                }
                if (paramTypes[i] != List.class) {
                    throw new RuntimeException("@FieldName 标注的参数类型必须是List类型");
                }
                String fieldName = ((FieldName) ann).value();
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
