package me.about.widget.config.configcenter;


import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.about.widget.config.ConfigChangeListener;
import me.about.widget.config.enums.PropertyChangeType;
import me.about.widget.config.model.ConfigChange;
import me.about.widget.config.model.ConfigChangeEvent;
import me.about.widget.config.util.ClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 配置服务
 *
 * @author: hugo.zxh
 * @date: 2023/11/02 14:23
 */
public class ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    private Properties oldProperties;

    private String namespace = "application.properties";

    private List<ConfigChangeListener> listeners = Lists.newCopyOnWriteArrayList();

    public static Properties getPropertiesInstance() {
        return new Properties();
    }

    private ConfigService() {
    }

    private static final ConfigService INSTANCE = new ConfigService();

    public static ConfigService getInstance() {
        return INSTANCE;
    }


    public void startSchedule() {
        Executors.newScheduledThreadPool(1)
                .scheduleWithFixedDelay(() -> checkProperties(),1,5, TimeUnit.SECONDS);
    }

    private void checkProperties() {
        Properties properties = loadFromResource();
        onRepositoryChange(properties);
    }

    private Properties loadFromResource() {
//        String name = String.format("META-INF/config/%s.properties", namespace);
        InputStream in = ClassLoaderUtil.getLoader().getResourceAsStream(namespace);
        Properties properties = new Properties();
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException ex) {
                logger.error("Load resource config for namespace {} failed", namespace, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
        return properties;
    }

    private void onRepositoryChange(Properties newProperties) {
        if (newProperties.equals(oldProperties)) {
            return;
        }
        List<ConfigChange> changes = calcPropertyChanges("default", oldProperties, newProperties);
        Map<String, ConfigChange> changeMap = Maps.uniqueIndex(changes, ConfigChange::getPropertyName);
        updateConfig(newProperties);
        fireConfigChange(new ConfigChangeEvent("default", changeMap));
    }

    public void addConfigChangeListener(ConfigChangeListener configChangeListener) {
        listeners.add(configChangeListener);
    }

    void fireConfigChange(ConfigChangeEvent changeEvent) {
        for (ConfigChangeListener changeListener : listeners) {
            changeListener.onChange(changeEvent);
        }
    }

    private void updateConfig(Properties newProperties) {
        oldProperties = newProperties;
    }

    private List<ConfigChange> calcPropertyChanges(String namespace, Properties previous,Properties current) {
        if (previous == null) {
            previous = getPropertiesInstance();
        }
        if (current == null) {
            current =  getPropertiesInstance();
        }
        Set<String> previousKeys = previous.stringPropertyNames();
        Set<String> currentKeys = current.stringPropertyNames();

        Set<String> commonKeys = Sets.intersection(previousKeys, currentKeys);
        Set<String> newKeys = Sets.difference(currentKeys, commonKeys);
        Set<String> removedKeys = Sets.difference(previousKeys, commonKeys);

        List<ConfigChange> changes = Lists.newArrayList();

        for (String newKey : newKeys) {
            changes.add(new ConfigChange(namespace, newKey, null, current.getProperty(newKey),
                    PropertyChangeType.ADDED));
        }

        for (String removedKey : removedKeys) {
            changes.add(new ConfigChange(namespace, removedKey, previous.getProperty(removedKey), null,
                    PropertyChangeType.DELETED));
        }

        for (String commonKey : commonKeys) {
            String previousValue = previous.getProperty(commonKey);
            String currentValue = current.getProperty(commonKey);
            if (Objects.equal(previousValue, currentValue)) {
                continue;
            }
            changes.add(new ConfigChange(namespace, commonKey, previousValue, currentValue,
                    PropertyChangeType.MODIFIED));
        }
        return changes;
    }

}
