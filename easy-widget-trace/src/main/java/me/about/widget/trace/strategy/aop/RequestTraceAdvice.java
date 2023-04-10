package me.about.widget.trace.strategy.aop;


import me.about.widget.trace.entity.Trace;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * java中的注解里面的值只能是一个常量，所以通过读配置的形式来设置切面值
 *
 * @author: hugo.zxh
 * @date: 2022/11/02 11:29
 * @description:
 */
public class RequestTraceAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getTypeName()).append('.');
        sb.append(method.getName());
        String fullMethodName = sb.toString();
        Trace.enter(fullMethodName);
        try {
            Object proceed = invocation.proceed();
            Trace.exit();
            return proceed;
        } catch (Throwable e) {
            Trace.exit("throw exception:" + e.getMessage());
            throw e;
        }
    }
}
