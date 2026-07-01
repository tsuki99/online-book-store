package mate.academy.springbootweb.service.shoppingcart;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.cartitem.AddCartItemRequestDto;
import mate.academy.springbootweb.dto.cartitem.CartItemDto;
import mate.academy.springbootweb.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootweb.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.mapper.CartItemMapper;
import mate.academy.springbootweb.mapper.ShoppingCartMapper;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.model.CartItem;
import mate.academy.springbootweb.model.ShoppingCart;
import mate.academy.springbootweb.model.User;
import mate.academy.springbootweb.repository.book.BookRepository;
import mate.academy.springbootweb.repository.cartitem.CartItemRepository;
import mate.academy.springbootweb.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbootweb.security.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final String NOT_FOUND_SHOPPING_CART_MESSAGE =
            "Can't find shopping cart by this user";
    private static final String NOT_FOUND_CART_ITEM_MESSAGE = "Can't find cart item by id: ";
    private static final String NOT_FOUND_BOOK_MESSAGE = "Can't find book by id: ";
    private final SecurityUtil securityUtil;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto retrieveUserShoppingCart() {
        return shoppingCartMapper.toDto(getShoppingCart());
    }

    @Override
    public CartItemDto addBooksToShoppingCart(AddCartItemRequestDto requestDto) {
        ShoppingCart userShoppingCart = getShoppingCart();

        Optional<CartItem> existingItem =
                cartItemRepository.findByShoppingCartIdAndBookId(
                        userShoppingCart.getId(),
                        requestDto.getBookId()
                );

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());

            return cartItemMapper.toDto(cartItem);
        }

        Long bookId = requestDto.getBookId();
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException(NOT_FOUND_BOOK_MESSAGE + bookId));

        CartItem newItem = new CartItem();
        newItem.setBook(book);
        newItem.setShoppingCart(userShoppingCart);
        newItem.setQuantity(requestDto.getQuantity());

        CartItem savedItem = cartItemRepository.save(newItem);

        return cartItemMapper.toDto(savedItem);
    }

    @Override
    public CartItemDto updateBookQuantityByCartItemId(
            Long id, UpdateCartItemRequestDto requestDto
    ) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(NOT_FOUND_CART_ITEM_MESSAGE + id));

        validateCartItemOwnership(cartItem.getShoppingCart().getUser().getId());

        cartItemMapper.updateCartItemFromDto(cartItem, requestDto);

        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public void deleteBookFromShoppingCart(Long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(NOT_FOUND_CART_ITEM_MESSAGE + id));

        validateCartItemOwnership(cartItem.getShoppingCart().getUser().getId());

        cartItemRepository.delete(cartItem);
    }

    private ShoppingCart getShoppingCart() {
        User user = securityUtil.getCurrentUser();

        return shoppingCartRepository.findByUserId(user.getId()).orElseThrow(
                        () -> new EntityNotFoundException(NOT_FOUND_SHOPPING_CART_MESSAGE)
        );
    }

    private void validateCartItemOwnership(Long ownerId) {
        if (!ownerId.equals(securityUtil.getCurrentUser().getId())) {
            throw new AccessDeniedException("Not your cart item");
        }
    }
}
