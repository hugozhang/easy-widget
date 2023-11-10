package me.about.widget.config.refresh;

import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * RefreshBeanEvent 事件
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 23:48
 */
public class RefreshBeanEvent extends ApplicationEvent {

    private Set<String> keys;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public RefreshBeanEvent(Object source,Set<String> keys) {
        super(source);
        this.keys = keys;
    }

    public Set<String> getKeys() {
        return keys;
    }
}
