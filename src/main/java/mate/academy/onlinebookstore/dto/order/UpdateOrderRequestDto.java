package mate.academy.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.onlinebookstore.model.enums.Status;

@Data
public class UpdateOrderRequestDto {
    @NotNull
    private Status status;
}
