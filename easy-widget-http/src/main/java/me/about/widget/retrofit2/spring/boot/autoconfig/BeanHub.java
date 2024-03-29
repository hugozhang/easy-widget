package me.about.widget.retrofit2.spring.boot.autoconfig;

import me.about.widget.retrofit2.spring.AutoConfiguredRetrofitScannerRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * bean 收集器
 *
 * @author: hugo.zxh
 * @date: 2023/02/08 11:01
 * @description:
 */
public class BeanHub implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {
                AutoConfiguredRetrofitScannerRegistrar.class.getName()
        };
    }

}
