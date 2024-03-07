package me.about.widget.spring.validation;

import me.about.widget.spring.validation.annotation.PositiveNumberList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PositiveNumberListValidator implements ConstraintValidator<PositiveNumberList,List<? extends Number>> {

    @Override
    public void initialize(PositiveNumberList constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<? extends Number> numbers, ConstraintValidatorContext context) {
        if (numbers == null) {
            return true;
        }
        for (Number number : numbers) {
            if (number instanceof Integer) {
                if ((Integer) number < 0) {
                    return false;
                }
            } else if (number instanceof Long) {
                if ((Long) number < 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
