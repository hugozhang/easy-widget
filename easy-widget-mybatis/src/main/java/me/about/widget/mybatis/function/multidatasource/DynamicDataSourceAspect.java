package me.about.widget.mybatis.function.multidatasource;


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
 * 多数据源AOP
 *
 * @author: hugo.zxh
 * @date: 2020/11/09 14:23
 * @description:
 */
@Component
@Aspect
@Order(-1)
public class DynamicDataSourceAspect {

    @Pointcut("@annotation(me.about.widget.mybatis.function.multidatasource.DbSwitch)")
    public void dsPointCut()  {
    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method targetMethod = this.getTargetMethod(point);
        DbSwitch dataSource = targetMethod.getAnnotation(DbSwitch.class);
        DynamicDataSource.setDataSourceKey(dataSource.value());
        try {
            return point.proceed();
        }
        finally  {
            DynamicDataSource.setDataSourceKey(DbTypeEnum.DB1);
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
