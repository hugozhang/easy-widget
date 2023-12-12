package me.about.widget.multicache.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * spring SpEL 表达式解析
 *
 * @author: hugo.zxh
 * @date: 2022/12/02 16:33
 * @description:
 */
public class SpelParser {

    /**
     * key 支持SPEL表达式
     * @param key
     * @param method
     * @param args
     * @return
     */
    public static String parseKey(String key, Method method, Object [] args) {
        if(StringUtils.isBlank(key)) {
            return null;
        }

        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = Optional.ofNullable(u.getParameterNames(method)).orElse(new String[0]);

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
