import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Created with IntelliJ IDEA.
 *
 * @auther: hugo.zxh
 * @date: 2022/06/30 16:55
 * @description:
 */
public class Main {

    public static void main(String[] args) {
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        context.setVariable("name", "java");
        context.setVariable("age", "20");
        String value = parser.parseExpression("#name + '' + #name + '' + #age").getValue(context, String.class);
        System.out.println(value);
    }

}
