package mate.academy.springbootweb.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {
    private static final Pattern ISBN_PATTERN = Pattern.compile(
            "^(97(8|9))?[\\d-]{10,17}[\\dX]$");

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        return ISBN_PATTERN.matcher(isbn).matches();
    }
}
