package me.about.widget.lock.redis.support.spring;

import me.about.widget.lock.redis.RedisLockContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/13 17:45
 * @Description:
 */

@Aspect
public class RedisLockAspect {

    @Resource
    private RedisLockContext redisLockContext;

    @Pointcut("@annotation(me.about.widget.lock.redis.support.spring.annotation.DLock)")
    public void dLock() {

    }

    @Around("dLock()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Lock lock = redisLockContext.getLock("hello");
        try {
            lock.lock();
            return pjp.proceed();
        } finally {
            lock.unlock();
        }
    }
}
