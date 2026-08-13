package mate.academy.onlinebookstore.service.shoppingcart;

import mate.academy.onlinebookstore.dto.cartitem.AddCartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto retrieveUserShoppingCart();

    ShoppingCartDto addBooksToShoppingCart(AddCartItemRequestDto requestDto);

    ShoppingCartDto updateBookQuantityByCartItemId(Long id, UpdateCartItemRequestDto requestDto);

    void deleteBookFromShoppingCart(Long id);

    void clear();
}
