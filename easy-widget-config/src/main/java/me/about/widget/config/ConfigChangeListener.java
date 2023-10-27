package me.about.widget.config;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;

/**
 * 配置变更监听器
 *
 * @author: hugo.zxh
 * @date: 2023/10/20 14:28
 */
public interface ConfigChangeListener {

    /**
     * Invoked when there is any config change
     * @param changeEvent the event for this change
     */
    void onChange(ConfigChangeEvent changeEvent);
}
