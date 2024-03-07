package me.about.widget.spring.validation;

import me.about.widget.spring.validation.annotation.EnumString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class EnumStringValidator implements ConstraintValidator<EnumString, String> {
    private List<String> enumStringList;

    @Override
    public void initialize(EnumString constraintAnnotation) {
        enumStringList = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }
        return enumStringList.contains(value);
    }
}

