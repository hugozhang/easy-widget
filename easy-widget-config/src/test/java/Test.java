import com.ctrip.framework.apollo.core.utils.PropertiesUtil;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/03/19 18:47
 */
public class Test {

    public static void main(String[] args) throws IOException {

        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:config/*.txt");

//                Resource resource = new ClassPathResource("classpath:config/*.txt");

        System.out.println(resources);


        MutablePropertySources mutablePropertySources = new MutablePropertySources();
        Map<String, Object> map = new HashMap<>(8);
        map.put("name", "throwable");
        map.put("age", 25);
        MapPropertySource mapPropertySource = new MapPropertySource("map", map);
        mutablePropertySources.addLast(mapPropertySource);
        Properties properties = new Properties();
        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource("prop", properties);
        properties.put("name", "doge");
        properties.put("gourp", "group-a");
        mutablePropertySources.addBefore("map", propertiesPropertySource);
        System.out.println(mutablePropertySources);


        String string = PropertiesUtil.toString(properties);
        System.out.println(string);

    }

}
