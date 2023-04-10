package me.about.widget.config.spi;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.config.spring.SpringValueRefresh;
import me.about.widget.config.util.ConfigKeys;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk配置资源加载
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 13:59
 * @description:
 */
@Slf4j
public class ZookeeperPropertySourceLocator implements PropertySourceLocator {

    /**
     * zk 目录
     */
    private static final String APP_DATA_NODE = "/";

    /**
     * zk 区分应用目录
     */
    private static final String NAMESPACE = "polaris_conf";

    private CuratorFramework curator;

    public ZookeeperPropertySourceLocator() {
        String zkAddr = System.getProperty(ConfigKeys.ZOOKEEPER_ADDRESS);
        if (zkAddr != null) {
            curator = CuratorFrameworkFactory.builder().connectString(zkAddr)
                    .sessionTimeoutMs(50000)
                    .connectionTimeoutMs(5000)
                    .retryPolicy(new ExponentialBackoffRetry(5000, 5))
                    .namespace(NAMESPACE)
                    .build();
            curator.start();
        }
    }

    @Override
    public PropertySource<?> locate(Environment environment, ConfigurableApplicationContext applicationContext) {
        Map<String,Object> dataMap = new HashMap<>(128);
        CompositePropertySource composite = new CompositePropertySource("remoteConfigService");
        if (curator == null) {
            MapPropertySource mapPropertySource = new MapPropertySource("remoteConfigService",dataMap);
            composite.addPropertySource(mapPropertySource);
            return composite;
        }
        try {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(curator, APP_DATA_NODE, true);
            pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            List<ChildData> childDataList = pathChildrenCache.getCurrentData();
            childDataList.forEach(x -> {
                String path = x.getPath();
                String value = new String(x.getData(), StandardCharsets.UTF_8);
                log.info("\t** 子节点路径 , childPath => " + path +" , childData => " + value);
                String key = SpringValueRefresh.getKey(environment, path);
                if (key != null) {
                    dataMap.put(key,value);
                }
            });

            MapPropertySource mapPropertySource = new MapPropertySource("remoteConfigService",dataMap);
            composite.addPropertySource(mapPropertySource);

            addListener(pathChildrenCache,environment,applicationContext);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return composite;
    }

    private void addListener(PathChildrenCache pathChildrenCache,Environment environment, ConfigurableApplicationContext applicationContext) {
        ZookeeperPathChildrenCacheListener pathChildrenCacheListener = new ZookeeperPathChildrenCacheListener(environment,applicationContext);
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
    }
}
