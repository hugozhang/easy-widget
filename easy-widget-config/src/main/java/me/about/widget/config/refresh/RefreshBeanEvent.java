package me.about.widget.config.refresh;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * RefreshBeanEvent 事件
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 23:48
 */
@Getter
public class RefreshBeanEvent extends ApplicationEvent {

    private final Set<String> keys;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public RefreshBeanEvent(Object source,Set<String> keys) {
        super(source);
        this.keys = keys;
    }

}
