package me.about.widget.lock.redis.support.spring;

import me.about.widget.lock.LockContext;
import me.about.widget.lock.LockException;
import me.about.widget.lock.redis.support.spring.annotation.DLock;
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
import java.util.concurrent.TimeUnit;
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

    private final DLockExpressionEvaluator evaluator = new DLockExpressionEvaluator();

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
        //Signature 其实有很多子类的，但是因为这里注解只能在方法上，所有不用检查instance of MethodSignature
        MethodSignature methodSignature = (MethodSignature)signature;

        //接口方法
        Method targetMethod = methodSignature.getMethod();

        //实现类方法
        Method method = targetClass.getDeclaredMethod(methodSignature.getName() , methodSignature.getParameterTypes());

        DLock dLock = targetMethod.getAnnotation(DLock.class);

        //Spring EL 表达式支持
        EvaluationContext context = evaluator.createEvaluationContext(method, args, targetObject, targetClass, targetMethod, new Object(), beanFactory);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(targetMethod, targetClass);
        Object key = evaluator.key(dLock.key(), methodKey, context);

        Assert.notNull(key,"@DLock key must not be null.");

        Lock lock = lockContext.getLock(key.toString());
        try {
            if(lock.tryLock(5, TimeUnit.MINUTES)) {
                return pjp.proceed();
            } else {
                throw new LockException("Acquire Lock Timeout",key.toString(),Thread.currentThread().getName());
            }
        } finally {
            lock.unlock();
        }
    }
}
