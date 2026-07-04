package mate.academy.springbootweb.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.springbootweb.model.enums.Status;

@Data
public class UpdateOrderRequestDto {
    @NotNull
    private Status status;
}
