import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @auther: hugo.zxh
 * @date: 2022/06/30 16:55
 * @description:
 */
public class Main2 {

    public static void main(String[] args) {
        // 假设这是您的输入列表
        List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");

        Map<String, Object> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, null);
        }
        System.out.println(map.values());

        // 将列表转换为Map，其中每个键的初始值为null
//        Map<String, Object> map = keys.stream()
//                .collect(Collectors.toMap(
//                        key -> key,
//                        value -> null
//                ));

        // 打印结果
        map.forEach((k, v) -> System.out.println(k + " = " + v));
    }

}
