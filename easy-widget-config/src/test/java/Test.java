import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

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

    }

}
