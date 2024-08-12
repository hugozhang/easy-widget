package me.about.widget.jobflow.annotation;

import me.about.widget.jobflow.core.Task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DependsOn {
    Class<? extends Task>[] value();
}
