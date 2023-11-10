package me.about.widget.config.refresh.config.annotation;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.*;

/**
 *
 * {@link me.about.widget.config.refresh.RefreshBeanScope refresh scope}.
 *
 * @author: hugo.zxh
 * @date: 2023/11/09 17:08
 */

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Scope("refreshBean")
@Documented
public @interface RefreshBeanScope {

    /**
     * @see Scope#proxyMode()
     * @return proxy mode
     */
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;

}
