package mate.academy.springbootweb.exception;

import java.util.List;
import mate.academy.springbootweb.dto.error.ErrorResponse;
import mate.academy.springbootweb.dto.error.FieldErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<FieldErrorDto> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldErrorDto(e.getField(), e.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }
}
