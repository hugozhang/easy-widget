package me.about.widget.spring.boot.autoconfig;

import org.springframework.context.annotation.Import;

/**
 * spring boot auto config
 *
 * @Author: hugo.zxh
 * @Date: 2022/02/26 21:12
 * @Description:
 */
@Import(value = BeanHub.class)
public class BeanAutoConfiguration {

}
