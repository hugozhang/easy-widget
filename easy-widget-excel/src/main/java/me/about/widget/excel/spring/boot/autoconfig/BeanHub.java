package me.about.widget.excel.spring.boot.autoconfig;

import me.about.widget.excel.spring.MvcConfig;
import me.about.widget.excel.spring.support.ExcelResponseBodyAdvice;
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
                ExcelResponseBodyAdvice.class.getName(),
                MvcConfig.class.getName()
        };
    }
}