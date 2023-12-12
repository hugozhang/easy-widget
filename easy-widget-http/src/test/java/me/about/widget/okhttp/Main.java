package me.about.widget.okhttp;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class Main {

    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();

        String baseUrl = parser.parseExpression("#{T(com.example.UrlUtils).getHost('http://127.0.0.1/api/v3/sys/config')}").getValue(context, String.class);
        System.out.println(baseUrl);

    }

}
