import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableList;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
        String valueExpr = parser.parseExpression("#name + '' + #name + '' + #age").getValue(context, String.class);
        System.out.println(valueExpr);


        // 假设 caffeineCache 是你的缓存实例
        Cache<String, String> caffeineCache = Caffeine.newBuilder().build();

// 添加一些数据到缓存中
        caffeineCache.put("1key1", "value1");
        caffeineCache.put("2key2", "value2");

// 定义要检索的键的 Iterable
        Iterable<String> keys = ImmutableList.of("1key1", "1key3");

        Map<String, String> values = caffeineCache.getAllPresent(keys);

        Map<String, String> valuesWithNulls = new HashMap<>();
        keys.forEach(key -> {
            valuesWithNulls.put(key, values.getOrDefault(key, null));
        });
// 打印包含 null 的结果
        valuesWithNulls.forEach((key, value) -> System.out.println(key + " = " + value));

        System.out.println("valuesWithNulls = " + valuesWithNulls.values());

// 定义 mappingFunction，用于处理不存在的键
        Function<Iterable<? extends String>, Map<String, String>> mappingFunction =
                (missingKeys) -> {
                    Map<String, String> computedValues = new HashMap<>();
                    // 这里可以定义当键缺失时如何计算值的逻辑
                    // 例如，从数据库加载或使用默认值
                    missingKeys.forEach(missingKey ->
                            computedValues.put(missingKey, "defaultValueFor" + missingKey));
                    return computedValues;
                };

// 使用 getAll 方法检索键的值
        Map<String, String> values1 = caffeineCache.getAll(keys, mappingFunction);

// 打印结果
        values1.forEach((key, value) -> System.out.println(key + " = " + value));
    }

}
