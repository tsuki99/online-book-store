package mate.academy.springbootweb.service.shoppingcart;

import mate.academy.springbootweb.dto.cartitem.AddCartItemRequestDto;
import mate.academy.springbootweb.dto.cartitem.CartItemDto;
import mate.academy.springbootweb.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootweb.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto retrieveUserShoppingCart();

    CartItemDto addBooksToShoppingCart(AddCartItemRequestDto requestDto);

    CartItemDto updateBookQuantityByCartItemId(Long id, UpdateCartItemRequestDto requestDto);

    void deleteBookFromShoppingCart(Long id);
}
