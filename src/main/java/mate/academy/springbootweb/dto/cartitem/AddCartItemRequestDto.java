package mate.academy.springbootweb.dto.cartitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCartItemRequestDto {
    @NotNull
    private Long bookId;
    @Min(1)
    private int quantity;
}
