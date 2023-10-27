package me.about.widget.config.spi;

import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置中心
 *
 * 注意这里的log不是slf4j的，因为这个时候slf4j还没有初始化
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 13:48
 * @description:
 */
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final Log logger;

    private final Properties properties = new Properties();

    private PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

    private final String propertiesFile = "classpath:config/**/*.properties";

    public CustomEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.logger = logFactory.getLog(CustomEnvironmentPostProcessor.class);
    }

    @SneakyThrows
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//        Resource resource = new ClassPathResource(propertiesFile);
        Resource[] resources = pathMatchingResourcePatternResolver.getResources(propertiesFile);
        for (Resource resource : resources) {
            logger.info("load local config:" + resource.getFilename());
            environment.getPropertySources().addLast(loadProperties(resource));
        }
    }

    private PropertySource<?> loadProperties(Resource resource) {
        if(!resource.exists()){
            throw new RuntimeException("file not exist");
        }
        try {
            properties.load(resource.getInputStream());
            return new PropertiesPropertySource("localConfigService",properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
