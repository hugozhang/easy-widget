package me.about.widget.spring.expression;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/30 17:42
 * @Description:
 */
public class SpELAspectContext {

    private ExpressionEvaluator evaluator = new ExpressionEvaluator();

    @Resource
    private BeanFactory beanFactory;

    public Object argKey(String argKey,ProceedingJoinPoint pjp) throws Throwable {
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

        //Spring EL 表达式支持
        EvaluationContext context = evaluator.createEvaluationContext(declaredMethod, args, targetObject, targetClass, targetMethod, new Object(), beanFactory);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(targetMethod, targetClass);
        Object key = evaluator.key(argKey, methodKey, context);
        Assert.notNull(key,"@DLock key must not be null.");
        return key;
    }
}
