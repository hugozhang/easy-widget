package me.about.widget.cache.annotation.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import me.about.widget.cache.annotation.Cached;
import me.about.widget.cache.annotation.FieldName;
import me.about.widget.cache.enums.CacheType;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CachedAnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                FieldName.class.getName(),
                Cached.class.getName()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element elem : roundEnv.getElementsAnnotatedWith(Cached.class)) {
            // 检查是否是方法
            if (elem instanceof ExecutableElement) {
                ExecutableElement methodElem = (ExecutableElement) elem;
                Cached annotation = methodElem.getAnnotation(Cached.class);
                CacheType cacheType = annotation.type();
                boolean hasFieldNameAnnotation = false;
                int fieldNameAnnotationCount = 0;
                for (VariableElement param : methodElem.getParameters()) {
                    if (param.getAnnotation(FieldName.class) != null) {
                        hasFieldNameAnnotation = true;
                        fieldNameAnnotationCount++;
                        // 检查变量类型是否是List
                        if (!(param.asType().toString().startsWith("java.util.List"))) {
                            processingEnv.getMessager().printMessage(
                                    Diagnostic.Kind.ERROR,
                                    "@FieldName can only be applied to parameters of type List",
                                    elem
                            );
                        }
                    }
                }

                // 如果参数上有多个@FieldName注解
                if (fieldNameAnnotationCount > 1) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            String.format(
                                    "Method '%s' has more than one parameter with @FieldName annotation.",
                                    methodElem.getSimpleName()
                            ),
                            methodElem
                    );
                }
                if (hasFieldNameAnnotation) {
                    if (cacheType != CacheType.IN_QUERY) {
                        // 如果参数上有@FieldName注解，但cacheType不是IN_QUERY
                        processingEnv.getMessager().printMessage(
                                Diagnostic.Kind.ERROR,
                                "Method has @FieldName on parameter but @Cached.type is not CacheType.IN_QUERY: " + methodElem.getClass() + "==>" + methodElem.getSimpleName(),
                                methodElem
                        );
                    }
                    // 如果参数上有@FieldName注解且cacheType是IN_QUERY，可以在这里添加其他处理
                } else {
                    if (cacheType == CacheType.IN_QUERY) {
                        // 如果参数上没有@FieldName注解，但cacheType是IN_QUERY
                        processingEnv.getMessager().printMessage(
                                Diagnostic.Kind.ERROR,
                                "@Cached with type CacheType.IN_QUERY requires @FieldName on at least one parameter: " + methodElem.getClass() + "==>" + methodElem.getSimpleName(),
                                methodElem
                        );
                    }
                    // 如果参数上没有@FieldName注解且cacheType不是IN_QUERY，可以在这里添加其他处理
                }
            } else {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@Cached can only be applied to methods",
                        elem
                );
            }
        }

        return true;
    }
}