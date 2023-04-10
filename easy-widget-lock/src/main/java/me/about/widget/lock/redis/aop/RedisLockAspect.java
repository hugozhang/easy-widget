package me.about.widget.lock.redis.aop;

import me.about.widget.lock.LockContext;
import me.about.widget.lock.LockException;
import me.about.widget.lock.redis.annotation.DLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

/**
 * AOP拦截
 *
 * @author: hugo.zxh
 * @date: 2020/11/13 17:45
 * @description:
 */
@Component
@Aspect
public class RedisLockAspect {

    @Resource
    private LockContext lockContext;

    @Pointcut("@annotation(me.about.widget.lock.redis.annotation.DLock)")
    public void dLock() {
    }

    @Around("dLock()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();

        //Signature 其实有很多子类的，但是因为这里注解只能在方法上
        MethodSignature methodSignature = (MethodSignature)signature;
        //接口方法
        Method targetMethod = methodSignature.getMethod();
        //锁申明
        DLock dLock = targetMethod.getAnnotation(DLock.class);
        Object key = parseKey(dLock.key(),targetMethod,pjp.getArgs());
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

    /**
     * 获取缓存的key
     * key 定义在注解上，支持SPEL表达式
     * @return
     */
    private String parseKey(String key,Method method,Object [] args) {
        if(StringUtils.isEmpty(key)) {
            return null;
        }

        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);

        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for(int i=0;i<paraNameArr.length;i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context,String.class);
    }
}
