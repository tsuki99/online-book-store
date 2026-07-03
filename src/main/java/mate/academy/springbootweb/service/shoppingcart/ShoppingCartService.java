package mate.academy.springbootweb.service.shoppingcart;

import mate.academy.springbootweb.dto.cartitem.AddCartItemRequestDto;
import mate.academy.springbootweb.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootweb.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootweb.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto retrieveUserShoppingCart();

    ShoppingCartDto addBooksToShoppingCart(AddCartItemRequestDto requestDto);

    ShoppingCartDto updateBookQuantityByCartItemId(Long id, UpdateCartItemRequestDto requestDto);

    void deleteBookFromShoppingCart(Long id);
}
