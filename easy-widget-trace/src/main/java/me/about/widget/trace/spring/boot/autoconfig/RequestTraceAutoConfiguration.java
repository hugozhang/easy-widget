package me.about.widget.trace.spring.boot.autoconfig;

import org.springframework.context.annotation.Import;

/**
 * 请求链路自动配置
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 21:12
 * @description:
 */
@Import(value = RequestTraceImportSelector.class)
public class RequestTraceAutoConfiguration {

}
