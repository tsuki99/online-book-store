package mate.academy.springbootweb.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddCartItemRequestDto {
    @NotNull
    @Positive
    private Long bookId;
    @Positive
    private int quantity;
}
