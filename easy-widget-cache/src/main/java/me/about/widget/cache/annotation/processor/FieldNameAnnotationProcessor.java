package me.about.widget.cache.annotation.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import me.about.widget.cache.annotation.FieldName;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FieldNameAnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(FieldName.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(FieldName.class)) {
            if (!(elem instanceof VariableElement)) {
                continue;
            }
            VariableElement varElem = (VariableElement) elem;
            // 检查变量类型是否是List
            if (!(varElem.asType().toString().startsWith("java.util.List"))) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@FieldName can only be applied to parameters of type List",
                        elem
                );
            }

//            FieldName fieldNameAnnotation = elem.getAnnotation(FieldName.class);
//            // 检查value属性是否为数组
//            String[] values = fieldNameAnnotation.value();
//            if (values.length == 1 && !values[0].isEmpty()) {
//                processingEnv.getMessager().printMessage(
//                        Diagnostic.Kind.ERROR,
//                        "@FieldName annotation value must be an array, but found single string: " + values[0],
//                        elem
//                );
//            }
        }
        return true;
    }
}
