package mate.academy.onlinebookstore.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.model.Role;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.model.enums.RoleName;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartProviderImpl;
import mate.academy.onlinebookstore.service.user.UserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartProviderTest {
    private static final String ENTITY_NOT_FOUND_MESSAGE = "Can't find shopping cart by user id: ";
    private static final Long ID_EXAMPLE = 100L;
    private static final Long NON_EXISTING_ID_EXAMPLE = 101L;
    @Mock
    private UserProvider userProvider;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    private ShoppingCartProviderImpl shoppingCartProvider;

    @BeforeEach
    void setUp() {
        shoppingCartProvider = new ShoppingCartProviderImpl(
                userProvider,
                shoppingCartRepository
        );
    }

    @Test
    @DisplayName("""
    Find shopping cart by existing user returns shopping cart
    """)
    void getUserShoppingCart_WithExistingUser_ReturnsShoppingCart() {
        ShoppingCart expectedShoppingCart = createShoppingCart();

        when(userProvider.getCurrentUser()).thenReturn(createUser(ID_EXAMPLE));
        when(shoppingCartRepository.findByUserId(ID_EXAMPLE))
                .thenReturn(Optional.of(expectedShoppingCart));

        ShoppingCart actualShoppingCart = shoppingCartProvider.getUserShoppingCart();

        assertEquals(expectedShoppingCart, actualShoppingCart);

        verify(userProvider).getCurrentUser();
        verify(shoppingCartRepository).findByUserId(ID_EXAMPLE);

        verifyNoMoreInteractions(userProvider, shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            Find shopping cart by non-existing user throws exception
    """)
    void getUserShoppingCart_WithNonExistingUser_ThrowsException() {
        when(userProvider.getCurrentUser()).thenReturn(createUser(NON_EXISTING_ID_EXAMPLE));
        when(shoppingCartRepository.findByUserId(NON_EXISTING_ID_EXAMPLE))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartProvider.getUserShoppingCart()
        );

        assertEquals(
                ENTITY_NOT_FOUND_MESSAGE + NON_EXISTING_ID_EXAMPLE,
                exception.getMessage()
        );

        verify(userProvider).getCurrentUser();
        verify(shoppingCartRepository).findByUserId(NON_EXISTING_ID_EXAMPLE);

        verifyNoMoreInteractions(userProvider, shoppingCartRepository);
    }

    private ShoppingCart createShoppingCart() {
        ShoppingCart cart = new ShoppingCart();
        CartItem item = new CartItem();

        cart.setId(ID_EXAMPLE);
        cart.setUser(createUser(ID_EXAMPLE));

        item.setId(ID_EXAMPLE);
        item.setBook(createBook());
        item.setQuantity(1);

        item.setShoppingCart(cart);
        cart.setCartItems(Set.of(item));

        return cart;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("testUser@i.ua");
        user.setPassword("$2a$10$g63Nfev5loA1MstylNkgM.6k3KC.O8Z7jBDO3xh/fmJrlKit9JH8S");
        user.setFirstName("TestUserFirstName");
        user.setLastName("TestUserLastName");
        user.setShippingAddress("Lomanosova st. 14");

        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.USER);

        user.setRoles(Set.of(userRole));
        user.setDeleted(false);

        return user;
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("The Hobbit");
        book.setAuthor("J.R.R. Tolkien");
        book.setIsbn("978000000001");
        book.setPrice(BigDecimal.valueOf(500));
        book.setDescription("Fantasy novel");
        book.setCoverImage("hobbit.jpg");
        book.setCategories(Set.of(new Category(1L)));

        return book;
    }
}
