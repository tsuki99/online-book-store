package mate.academy.onlinebookstore.service.shoppingcart;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.cartitem.AddCartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.cartitem.CartItemRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final String NOT_FOUND_CART_ITEM_MESSAGE = "Can't find cart item by id: ";
    private static final String NOT_FOUND_BOOK_MESSAGE = "Can't find book by id: ";
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartProvider shoppingCartProvider;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart newShoppingCart = new ShoppingCart();
        newShoppingCart.setUser(user);

        shoppingCartRepository.save(newShoppingCart);
    }

    @Override
    public ShoppingCartDto retrieveUserShoppingCart() {
        return shoppingCartMapper.toDto(getShoppingCart());
    }

    @Override
    public ShoppingCartDto addBooksToShoppingCart(AddCartItemRequestDto requestDto) {
        ShoppingCart userShoppingCart = getShoppingCart();

        Optional<CartItem> existingItem = userShoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(requestDto.getBookId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
        } else {
            Long bookId = requestDto.getBookId();

            CartItem newCartItem = new CartItem();
            newCartItem.setShoppingCart(userShoppingCart);
            newCartItem.setBook(bookRepository.findById(bookId).orElseThrow(
                    () -> new EntityNotFoundException(NOT_FOUND_BOOK_MESSAGE + bookId))
            );
            newCartItem.setQuantity(requestDto.getQuantity());

            userShoppingCart.getCartItems().add(newCartItem);
        }

        ShoppingCart savedShoppingCart = shoppingCartRepository.save(userShoppingCart);
        return shoppingCartMapper.toDto(savedShoppingCart);
    }

    @Override
    public ShoppingCartDto updateBookQuantityByCartItemId(
            Long id, UpdateCartItemRequestDto requestDto
    ) {
        ShoppingCart userShoppingCart = getShoppingCart();

        CartItem cartItem = getCartItem(id, userShoppingCart.getId());
        cartItem.setQuantity(requestDto.getQuantity());

        ShoppingCart savedShoppingCart = shoppingCartRepository.save(userShoppingCart);
        return shoppingCartMapper.toDto(savedShoppingCart);
    }

    @Override
    public void deleteBookFromShoppingCart(Long id) {
        ShoppingCart userShoppingCart = getShoppingCart();

        CartItem cartItem = getCartItem(id, userShoppingCart.getId());

        userShoppingCart.getCartItems().remove(cartItem);
        shoppingCartRepository.save(userShoppingCart);
    }

    @Override
    public void clear() {
        ShoppingCart userShoppingCart = getShoppingCart();

        userShoppingCart.getCartItems().clear();
        shoppingCartRepository.save(userShoppingCart);
    }

    private ShoppingCart getShoppingCart() {
        return shoppingCartProvider.getUserShoppingCart();
    }

    private CartItem getCartItem(Long cartItemId, Long shoppingCartId) {
        return cartItemRepository.findByIdAndShoppingCartId(
                        cartItemId,
                        shoppingCartId
                )
                .orElseThrow(
                        () -> new EntityNotFoundException(NOT_FOUND_CART_ITEM_MESSAGE + cartItemId)
                );
    }
}
