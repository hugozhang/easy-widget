package me.about.widget.config.model;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/10/20 14:25
 */
public class ConfigChangeEvent {

    private final String namespace;

    private final Map<String, ConfigChange> changes;

    public ConfigChangeEvent(String namespace, Map<String, ConfigChange> changes) {
        this.namespace = namespace;
        this.changes = changes;
    }

    public Set<String> changedKeys() {
        return changes.keySet();
    }

    public ConfigChange getChange(String key) {
        return changes.get(key);
    }

    public boolean isChanged(String key) {
        return changes.containsKey(key);
    }

    public String getNamespace() {
        return namespace;
    }

}
