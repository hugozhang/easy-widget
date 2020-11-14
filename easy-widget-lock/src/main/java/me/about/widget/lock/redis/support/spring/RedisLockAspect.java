package me.about.widget.lock.redis.support.spring;

import me.about.widget.lock.LockContext;
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
        MethodSignature methodSignature = (MethodSignature)signature;
        Method targetMethod = methodSignature.getMethod();

        DLock dLock = targetMethod.getAnnotation(DLock.class);

        EvaluationContext context = evaluator.createEvaluationContext(targetMethod, args, targetObject, targetClass, targetMethod, new Object(), beanFactory);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(targetMethod, targetClass);

        Object key = evaluator.key(dLock.key(), methodKey, context);
        assert key != null;

        Lock lock = lockContext.getLock(key.toString());
        try {
            lock.lock();
            return pjp.proceed();
        } finally {
            lock.unlock();
        }
    }
}
