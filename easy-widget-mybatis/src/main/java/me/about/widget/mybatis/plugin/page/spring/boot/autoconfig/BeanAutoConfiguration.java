package me.about.widget.mybatis.plugin.page.spring.boot.autoconfig;

import me.about.widget.mybatis.plugin.page.spring.PageMapperBeanPostProcessor;
import org.springframework.context.annotation.Import;

/**
 * 自动加载配置
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 9:49
 */
@Import(value = PageMapperBeanPostProcessor.class)
public class BeanAutoConfiguration {
}
