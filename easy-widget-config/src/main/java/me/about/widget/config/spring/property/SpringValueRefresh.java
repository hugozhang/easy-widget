package me.about.widget.config.spring.property;

import me.about.widget.config.util.ConfigKeys;

import org.springframework.core.env.Environment;

/**
 * SpringValueRefresh
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 23:22
 * @description:
 */
public class SpringValueRefresh {

    public static String getKey(Environment environment,String path) {
        String prefix = "/" + environment.getProperty(ConfigKeys.APP_NAME,"Polaris_hmap_web") + ".";
        return path.startsWith(prefix) ? path.substring(prefix.length()) : null;
    }

}
