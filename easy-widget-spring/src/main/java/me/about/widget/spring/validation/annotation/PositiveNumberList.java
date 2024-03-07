package me.about.widget.spring.validation.annotation;

import me.about.widget.spring.validation.PositiveNumberListValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveNumberListValidator.class)
public @interface PositiveNumberList {

    String message() default "List elements must be greater than 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
