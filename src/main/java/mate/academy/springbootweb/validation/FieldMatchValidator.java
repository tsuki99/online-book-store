package mate.academy.springbootweb.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String first;
    private String second;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.first = constraintAnnotation.first();
        this.second = constraintAnnotation.second();

    }

    @Override
    public boolean isValid(Object objectToValidate,
                           ConstraintValidatorContext constraintValidatorContext) {

        if (objectToValidate == null) {
            return true;
        }

        try {
            Field firstField = objectToValidate.getClass().getDeclaredField(first);
            Field secondField = objectToValidate.getClass().getDeclaredField(second);

            firstField.setAccessible(true);
            secondField.setAccessible(true);

            Object firstValue = firstField.get(objectToValidate);
            Object secondValue = secondField.get(objectToValidate);

            boolean valuesEquals = Objects.equals(firstValue, secondValue);

            if (!valuesEquals) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                        constraintValidatorContext.getDefaultConstraintMessageTemplate()
                        )
                        .addPropertyNode(second)
                        .addConstraintViolation();
            }

            return valuesEquals;

        } catch (Exception e) {
            return false;
        }
    }

}
