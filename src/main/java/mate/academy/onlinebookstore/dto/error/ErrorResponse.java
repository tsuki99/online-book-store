package mate.academy.onlinebookstore.dto.error;

import java.util.List;

public record ErrorResponse(List<FieldErrorDto> errors) {

}
