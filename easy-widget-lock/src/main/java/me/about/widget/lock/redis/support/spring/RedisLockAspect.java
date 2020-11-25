package me.about.widget.lock.redis.support.spring;

import io.netty.util.HashedWheelTimer;
import me.about.widget.lock.LockContext;
import me.about.widget.lock.LockException;
import me.about.widget.lock.redis.support.spring.annotation.DLock;
import me.about.widget.lock.redis.support.spring.expression.ExpressionEvaluator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.Method;
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

    private ExpressionEvaluator evaluator = new ExpressionEvaluator();

    private HashedWheelTimer timer = new HashedWheelTimer();

    @Resource
    private BeanFactory beanFactory;

    @Resource
    private LockContext lockContext;

    @Pointcut("@annotation(me.about.widget.lock.redis.support.spring.annotation.DLock)")
    public void dLock() {

    }

    @Around("dLock()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        Object targetObject = pjp.getTarget();
        Class<?> targetClass=pjp.getTarget().getClass();
        Object[] args = pjp.getArgs();

        Signature signature = pjp.getSignature();

        //安全检查
        if (!(signature instanceof MethodSignature)) {
            throw new RuntimeException("signature isn't instanceof MethodSignature.");
        }

        //Signature 其实有很多子类的，但是因为这里注解只能在方法上
        MethodSignature methodSignature = (MethodSignature)signature;

        //接口方法
        Method targetMethod = methodSignature.getMethod();

        //申明方法
        Method declaredMethod = targetClass.getDeclaredMethod(methodSignature.getName() , methodSignature.getParameterTypes());

        //锁申明
        DLock dLock = targetMethod.getAnnotation(DLock.class);

        //Spring EL 表达式支持
        EvaluationContext context = evaluator.createEvaluationContext(declaredMethod, args, targetObject, targetClass, targetMethod, new Object(), beanFactory);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(targetMethod, targetClass);
        Object key = evaluator.key(dLock.key(), methodKey, context);
        Assert.notNull(key,"@DLock key must not be null.");

        Lock lock = lockContext.getLock(key.toString());

        if(!lock.tryLock()) {
            throw new LockException("获取锁失败",key.toString(),Thread.currentThread().getName());
        }
        try {
            return pjp.proceed();
        } finally {
            lock.unlock();
        }
    }
}
