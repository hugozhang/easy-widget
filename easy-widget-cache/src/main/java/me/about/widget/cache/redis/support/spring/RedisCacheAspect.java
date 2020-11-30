package me.about.widget.cache.redis.support.spring;

import me.about.widget.cache.CacheService;
import me.about.widget.cache.redis.Hash;
import me.about.widget.cache.redis.support.spring.annotation.MyCacheEvict;
import me.about.widget.cache.redis.support.spring.annotation.MyCachePut;
import me.about.widget.cache.redis.support.spring.annotation.MyCacheable;
import me.about.widget.spring.expression.SpELAspectContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/30 17:04
 * @Description:
 */

@Aspect
public class RedisCacheAspect {

    @Resource
    private CacheService cacheService;

    @Resource
    private SpELAspectContext spELAspectContext;

    @Pointcut("@annotation(me.about.widget.cache.redis.support.spring.annotation.MyCacheable)")
    public void myCacheable() {
    }

    @Pointcut("@annotation(me.about.widget.cache.redis.support.spring.annotation.MyCachePut)")
    public void myCachePut() {
    }

    @Pointcut("@annotation(me.about.widget.cache.redis.support.spring.annotation.MyCacheEvict)")
    public void myCacheEvict() {
    }

    @Around("myCacheable()")
    public Object doMyCacheable(ProceedingJoinPoint pjp) throws Throwable {
        Method targetMethod = getMethod(pjp);
        MyCacheable myCacheable = targetMethod.getAnnotation(MyCacheable.class);
        String key = spELAspectContext.argKey(myCacheable.key(), pjp).toString();
        Hash hash = cacheService.hash(myCacheable.group());
        if (hash.hasKey(key)) {
            return hash.get(key);
        }
        Object value = pjp.proceed();
        hash.put(key,value,myCacheable.expire(),myCacheable.timeUnit());
        return value;
    }

    @Around("myCachePut()")
    public Object doMyCachePut(ProceedingJoinPoint pjp) throws Throwable {
        Method targetMethod = getMethod(pjp);
        MyCachePut myCachePut = targetMethod.getAnnotation(MyCachePut.class);
        String key = spELAspectContext.argKey(myCachePut.key(), pjp).toString();
        Hash hash = cacheService.hash(myCachePut.group());
        hash.put(myCachePut.group(),key,myCachePut.expire(),myCachePut.timeUnit());
        return pjp.proceed();
    }

    @Around("myCacheEvict()")
    public Object doMyCacheEvict(ProceedingJoinPoint pjp) throws Throwable {
        Method targetMethod = getMethod(pjp);
        MyCacheEvict myCacheEvict = targetMethod.getAnnotation(MyCacheEvict.class);
        String key = spELAspectContext.argKey(myCacheEvict.key(), pjp).toString();
        Hash hash = cacheService.hash(myCacheEvict.group());
        hash.delete(myCacheEvict.group(),key);
        return pjp.proceed();
    }

    private Method getMethod(ProceedingJoinPoint pjp) {
        Signature signature = pjp.getSignature();
        //安全检查
        if (!(signature instanceof MethodSignature)) {
            throw new RuntimeException("signature isn't instanceof MethodSignature.");
        }
        //Signature 其实有很多子类的，但是因为这里注解只能在方法上
        MethodSignature methodSignature = (MethodSignature) signature;
        //接口方法
        return methodSignature.getMethod();
    }
}
