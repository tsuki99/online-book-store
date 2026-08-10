package mate.academy.springbootweb.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.springbootweb.dto.cartitem.CartItemDto;

@Data
@Accessors(chain = true)
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
