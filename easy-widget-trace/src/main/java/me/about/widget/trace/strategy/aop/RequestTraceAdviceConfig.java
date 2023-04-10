package me.about.widget.trace.strategy.aop;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * aop切面值读取point设置  eg:trace.pointcut.ant-path = execution(public * com.winning.hmap.drg..*.*(..))
 * 参考：https://www.csdn.net/tags/MtjaQg4sMzI2NTItYmxvZwO0O0OO0O0O.html
 *
 * @author: hugo.zxh
 * @date: 2022/11/02 11:34
 * @description:
 */
@Configuration
@ConditionalOnProperty(prefix = "me.about.easy.widget.trace",name = "pointcut")
public class RequestTraceAdviceConfig {

    @Value("${me.about.easy.widget.trace.pointcut}")
    private String pointcut;

    @Bean
    public AspectJExpressionPointcutAdvisor configureAdvisor() {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(pointcut);
        advisor.setAdvice(new RequestTraceAdvice());
        return advisor;
    }

}
