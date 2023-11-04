package me.about.widget.routing.spring;

import me.about.widget.routing.RoutingContext;
import me.about.widget.routing.spring.annotation.HintRouting;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 注解式路由
 *
 * @author: hugo.zxh
 * @date: 2023/10/31 15:42
 */
@Component
@Aspect
@Order(-1)
public class RoutingDataSourceAspect {

    @Pointcut("@annotation(me.about.widget.routing.spring.annotation.HintRouting)")
    public void dsPointCut()  {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method targetMethod = this.getTargetMethod(point);
        HintRouting dataSource = targetMethod.getAnnotation(HintRouting.class);
        RoutingContext.setRoutingDatabase(dataSource.value());
        try {
            return point.proceed();
        }
        finally  {

        }
    }

    /**
     * 获取目标方法
     */
    private Method getTargetMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method agentMethod = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(agentMethod.getName(), agentMethod.getParameterTypes());
    }

}
