package me.about.widget.config.spi;

import me.about.widget.config.configcenter.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 启动完成
 *
 * @author: hugo.zxh
 * @date: 2023/11/02 18:13
 */
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(StartupApplicationListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("spring boot startup finished.");
        ConfigService.getInstance().startSchedule();
    }
}
