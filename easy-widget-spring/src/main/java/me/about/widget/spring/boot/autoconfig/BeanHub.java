package me.about.widget.spring.boot.autoconfig;

import me.about.widget.spring.mvc.advice.GlobalExceptionHandler;
import me.about.widget.spring.mvc.advice.JsonResultResponseBodyHandler;
import me.about.widget.spring.support.SpringContextHolder;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * spring web
 *
 * @Author: hugo.zxh
 * @Date: 2022/02/26 21:40
 * @Description:
 */
public class BeanHub implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {
                GlobalExceptionHandler.class.getName(),
                JsonResultResponseBodyHandler.class.getName(),
                SpringContextHolder.class.getName()
        };
    }
}