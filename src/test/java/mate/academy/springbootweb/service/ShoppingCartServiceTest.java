package mate.academy.springbootweb.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mate.academy.springbootweb.dto.cartitem.AddCartItemRequestDto;
import mate.academy.springbootweb.dto.cartitem.CartItemDto;
import mate.academy.springbootweb.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootweb.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.mapper.ShoppingCartMapper;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.model.CartItem;
import mate.academy.springbootweb.model.Category;
import mate.academy.springbootweb.model.Role;
import mate.academy.springbootweb.model.ShoppingCart;
import mate.academy.springbootweb.model.User;
import mate.academy.springbootweb.model.enums.RoleName;
import mate.academy.springbootweb.repository.book.BookRepository;
import mate.academy.springbootweb.repository.cartitem.CartItemRepository;
import mate.academy.springbootweb.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbootweb.service.shoppingcart.ShoppingCartProvider;
import mate.academy.springbootweb.service.shoppingcart.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    private static final String NOT_FOUND_CART_ITEM_MESSAGE = "Can't find cart item by id: ";
    private static final String NOT_FOUND_BOOK_MESSAGE = "Can't find book by id: ";
    private static final String FIRST_BOOK_TITLE_EXAMPLE = "The Hobbit";
    private static final String SECOND_BOOK_TITLE_EXAMPLE = "Spring in Action";
    private static final Long ID_EXAMPLE = 100L;
    private static final Long SECOND_ID_EXAMPLE = 200L;
    private static final Long NON_EXISTING_ID_EXAMPLE = 200L;
    private static final Long FIRST_BOOK_ID_EXAMPLE = 1L;
    private static final Long SECOND_BOOK_ID_EXAMPLE = 2L;
    private static final int FIRST_ARGUMENT_INDEX = 0;
    private static final int EMPTY_SHOPPING_CART_SIZE = 0;
    private static final int DEFAULT_QUANTITY = 1;
    private static final int INCREASED_QUANTITY = 2;
    private static final Long USER_ROLE_ID = 1L;
    private static final RoleName ROLE_NAME_USER = RoleName.USER;
    private static final String USER_EMAIL_EXAMPLE = "testUser@i.ua";
    private static final String USER_PASSWORD_EXAMPLE =
            "$2a$10$g63Nfev5loA1MstylNkgM.6k3KC.O8Z7jBDO3xh/fmJrlKit9JH8S";
    private static final String USER_FIRST_NAME_EXAMPLE = "TestUserFirstName";
    private static final String USER_LAST_NAME_EXAMPLE = "TestUserLastName";
    private static final String USER_SHIPPING_ADDRESS_EXAMPLE = "Lomanosova st. 14";
    private static final String FIRST_BOOK_AUTHOR_EXAMPLE = "J.R.R. Tolkien";
    private static final String FIRST_BOOK_ISBN_EXAMPLE = "978000000001";
    private static final BigDecimal FIRST_BOOK_PRICE_EXAMPLE = new BigDecimal("500.00");
    private static final String FIRST_BOOK_DESC_EXAMPLE = "Fantasy novel";
    private static final String FIRST_BOOK_COVER_IMAGE_EXAMPLE = "hobbit.jpg";
    private static final Long FIRST_CATEGORY_ID_EXAMPLE = 1L;
    private static final String SECOND_BOOK_AUTHOR_EXAMPLE = "Craig Walls";
    private static final String SECOND_BOOK_ISBN_EXAMPLE = "978000000002";
    private static final BigDecimal SECOND_BOOK_PRICE_EXAMPLE = new BigDecimal("900.00");
    private static final String SECOND_BOOK_DESC_EXAMPLE = "Spring Framework";
    private static final String SECOND_BOOK_COVER_IMAGE_EXAMPLE = "spring.jpg";
    private static final Long SECOND_CATEGORY_ID_EXAMPLE = 2L;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private ShoppingCartProvider shoppingCartProvider;
    private ShoppingCartServiceImpl shoppingCartService;

    @BeforeEach
    void setUp() {
        shoppingCartService = new ShoppingCartServiceImpl(
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                shoppingCartMapper,
                shoppingCartProvider
        );
    }

    @Test
    @DisplayName("""
            Create shopping cart with existing user add cart to repository
            """)
    void createShoppingCart_WithExistingUser_CallsRepository() {
        User existingUser = createUser(ID_EXAMPLE);
        ArgumentCaptor<ShoppingCart> captor = ArgumentCaptor.forClass(ShoppingCart.class);

        shoppingCartService.createShoppingCart(existingUser);

        verify(shoppingCartRepository).save(captor.capture());

        ShoppingCart actualShoppingCart = captor.getValue();

        assertEquals(existingUser, actualShoppingCart.getUser());

        verifyNoMoreInteractions(
                shoppingCartMapper,
                shoppingCartProvider,
                shoppingCartRepository,
                cartItemRepository,
                bookRepository
        );
    }

    @Test
    @DisplayName("""
            Retrieve shopping cart returns shopping cart DTO
            """)
    void retrieveUserShoppingCart_WithExistingShoppingCart_ReturnsShoppingCartDto() {
        ShoppingCart shoppingCart = createShoppingCart();
        ShoppingCartDto expectedShoppingCartDto = createShoppingCartDto();

        when(shoppingCartProvider.getUserShoppingCart()).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedShoppingCartDto);

        ShoppingCartDto actualShoppingCartDto = shoppingCartService.retrieveUserShoppingCart();

        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);

        verify(shoppingCartProvider).getUserShoppingCart();
        verify(shoppingCartMapper).toDto(shoppingCart);

        verifyNoMoreInteractions(
                shoppingCartMapper,
                shoppingCartProvider,
                shoppingCartRepository,
                cartItemRepository,
                bookRepository
        );
    }

    @Test
    @DisplayName("""
            Add new book to shopping cart returns updated shopping cart DTO
            """)
    void addBooksToShoppingCart_WithNewBook_ReturnsUpdatedShoppingCartDto() {
        ShoppingCart shoppingCart = createShoppingCart();
        Book book = createSecondBook();
        AddCartItemRequestDto addCartItemRequestDto = createAddCartItemRequestDto(
                SECOND_BOOK_ID_EXAMPLE,
                DEFAULT_QUANTITY
        );
        ShoppingCartDto expectedShoppingCartDto = createUpdatedShoppingCartDtoWithTwoBooks();

        when(shoppingCartProvider.getUserShoppingCart())
                .thenReturn(shoppingCart);
        when(bookRepository.findById(addCartItemRequestDto.getBookId()))
                .thenReturn(Optional.of(book));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(FIRST_ARGUMENT_INDEX));
        when(shoppingCartMapper.toDto(any(ShoppingCart.class)))
                .thenReturn(expectedShoppingCartDto);

        ArgumentCaptor<ShoppingCart> captor = ArgumentCaptor.forClass(ShoppingCart.class);

        ShoppingCartDto actualShoppingCartDto =
                shoppingCartService.addBooksToShoppingCart(addCartItemRequestDto);

        verify(shoppingCartRepository).save(captor.capture());
        ShoppingCart savedShoppingCart = captor.getValue();

        CartItem addedCartItem = savedShoppingCart.getCartItems()
                .stream()
                .filter(item -> item.getBook().getId().equals(SECOND_BOOK_ID_EXAMPLE))
                .findFirst()
                .orElseThrow();

        assertEquals(INCREASED_QUANTITY, savedShoppingCart.getCartItems().size());
        assertEquals(DEFAULT_QUANTITY, addedCartItem.getQuantity());
        assertEquals(book, addedCartItem.getBook());
        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);

        verify(shoppingCartProvider).getUserShoppingCart();
        verify(bookRepository).findById(SECOND_BOOK_ID_EXAMPLE);
        verify(shoppingCartMapper).toDto(savedShoppingCart);

        verifyNoMoreInteractions(
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                shoppingCartMapper,
                shoppingCartProvider
        );
    }

    @Test
    @DisplayName("""
            Add existing book to shopping cart increases book quantity
            """)
    void addBooksToShoppingCart_WithExistingBook_IncreasesQuantity() {
        ShoppingCart shoppingCart = createShoppingCart();
        AddCartItemRequestDto addCartItemRequestDto = createAddCartItemRequestDto(
                FIRST_BOOK_ID_EXAMPLE,
                DEFAULT_QUANTITY
        );
        ShoppingCartDto expectedShoppingCartDto = createUpdatedShoppingCartDtoWithSingleBook();

        when(shoppingCartProvider.getUserShoppingCart())
                .thenReturn(shoppingCart);
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(FIRST_ARGUMENT_INDEX));
        when(shoppingCartMapper.toDto(any(ShoppingCart.class)))
                .thenReturn(expectedShoppingCartDto);

        ArgumentCaptor<ShoppingCart> captor = ArgumentCaptor.forClass(ShoppingCart.class);

        ShoppingCartDto actualShoppingCartDto =
                shoppingCartService.addBooksToShoppingCart(addCartItemRequestDto);

        verify(shoppingCartRepository).save(captor.capture());
        ShoppingCart savedShoppingCart = captor.getValue();

        CartItem existingCartItem = savedShoppingCart.getCartItems()
                .stream()
                .filter(item -> item.getBook().getId().equals(FIRST_BOOK_ID_EXAMPLE))
                .findFirst()
                .orElseThrow();

        assertEquals(DEFAULT_QUANTITY, savedShoppingCart.getCartItems().size());
        assertEquals(INCREASED_QUANTITY, existingCartItem.getQuantity());
        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);

        verify(shoppingCartProvider).getUserShoppingCart();
        verify(shoppingCartMapper).toDto(savedShoppingCart);

        verifyNoMoreInteractions(
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                shoppingCartMapper,
                shoppingCartProvider
        );
    }

    @Test
    @DisplayName("""
            Add non-existing book to shopping cart throws exception
            """)
    void addBooksToShoppingCart_WithNonExistingBook_ThrowsException() {
        ShoppingCart shoppingCart = createShoppingCart();
        AddCartItemRequestDto addCartItemRequestDto = createAddCartItemRequestDto(
                NON_EXISTING_ID_EXAMPLE,
                DEFAULT_QUANTITY
        );

        when(shoppingCartProvider.getUserShoppingCart())
                .thenReturn(shoppingCart);
        when(bookRepository.findById(NON_EXISTING_ID_EXAMPLE)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.addBooksToShoppingCart(addCartItemRequestDto)
        );

        assertEquals(
                NOT_FOUND_BOOK_MESSAGE + NON_EXISTING_ID_EXAMPLE,
                exception.getMessage()
        );

        verify(shoppingCartProvider).getUserShoppingCart();
        verify(bookRepository).findById(NON_EXISTING_ID_EXAMPLE);
        verify(shoppingCartRepository, never()).save(any(ShoppingCart.class));
        verify(shoppingCartMapper, never()).toDto(any(ShoppingCart.class));

        verifyNoMoreInteractions(
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                shoppingCartMapper,
                shoppingCartProvider
        );
    }

    @Test
    @DisplayName("""
            Update existing cart item quantity returns updated shopping cart DTO
            """)
    void updateBookQuantityByCartItemId_WithExistingCartItemId_ReturnsShoppingCartDto() {
        ShoppingCart shoppingCart = createShoppingCart();
        Optional<CartItem> optionalCartItem = shoppingCart.getCartItems().stream().findFirst();
        ShoppingCartDto expectedShoppingCartDto = createUpdatedShoppingCartDtoWithSingleBook();

        when(shoppingCartProvider.getUserShoppingCart())
                .thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(ID_EXAMPLE, ID_EXAMPLE))
                .thenReturn(optionalCartItem);
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(FIRST_ARGUMENT_INDEX));
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(expectedShoppingCartDto);

        ArgumentCaptor<ShoppingCart> captor = ArgumentCaptor.forClass(ShoppingCart.class);

        ShoppingCartDto actualShoppingCartDto = shoppingCartService.updateBookQuantityByCartItemId(
                ID_EXAMPLE,
                createUpdateCartItemRequestDto()
        );

        verify(shoppingCartRepository).save(captor.capture());
        ShoppingCart updatedShoppingCart = captor.getValue();

        CartItem updatedCartItem = updatedShoppingCart.getCartItems()
                .stream()
                .filter(item -> item.getBook().getId().equals(FIRST_BOOK_ID_EXAMPLE))
                .findFirst()
                .orElseThrow();

        assertEquals(INCREASED_QUANTITY, updatedCartItem.getQuantity());
        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);

        verify(shoppingCartProvider).getUserShoppingCart();
        verify(cartItemRepository).findByIdAndShoppingCartId(ID_EXAMPLE, ID_EXAMPLE);
        verify(shoppingCartMapper).toDto(shoppingCart);

        verifyNoMoreInteractions(
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                shoppingCartMapper,
                shoppingCartProvider
        );
    }

    @Test
    @DisplayName("""
            Delete existing cart item from shopping cart
            """)
    void deleteBookFromShoppingCart_WithExistingCartItemId_DeletesBook() {
        ShoppingCart shoppingCart = createShoppingCart();
        Optional<CartItem> optionalCartItem = shoppingCart.getCartItems().stream().findFirst();

        when(shoppingCartProvider.getUserShoppingCart())
                .thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(ID_EXAMPLE, ID_EXAMPLE))
                .thenReturn(optionalCartItem);
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(FIRST_ARGUMENT_INDEX));

        ArgumentCaptor<ShoppingCart> captor = ArgumentCaptor.forClass(ShoppingCart.class);

        shoppingCartService.deleteBookFromShoppingCart(ID_EXAMPLE);

        verify(shoppingCartRepository).save(captor.capture());
        ShoppingCart updatedShoppingCart = captor.getValue();

        assertEquals(EMPTY_SHOPPING_CART_SIZE, updatedShoppingCart.getCartItems().size());

        verify(shoppingCartProvider).getUserShoppingCart();
        verify(cartItemRepository).findByIdAndShoppingCartId(ID_EXAMPLE, ID_EXAMPLE);

        verifyNoMoreInteractions(
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                shoppingCartMapper,
                shoppingCartProvider
        );
    }

    @Test
    @DisplayName("""
            Delete non-existing cart item from shopping cart
            """)
    void deleteBookFromShoppingCart_WithNonExistingCartItemId_ThrowsException() {
        ShoppingCart shoppingCart = createShoppingCart();

        when(shoppingCartProvider.getUserShoppingCart())
                .thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(NON_EXISTING_ID_EXAMPLE, ID_EXAMPLE))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.deleteBookFromShoppingCart(NON_EXISTING_ID_EXAMPLE)
        );

        assertEquals(NOT_FOUND_CART_ITEM_MESSAGE + NON_EXISTING_ID_EXAMPLE,
                exception.getMessage()
        );

        verify(shoppingCartProvider).getUserShoppingCart();
        verify(cartItemRepository).findByIdAndShoppingCartId(NON_EXISTING_ID_EXAMPLE, ID_EXAMPLE);
        verify(shoppingCartRepository, never()).save(any(ShoppingCart.class));

        verifyNoMoreInteractions(
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                shoppingCartMapper,
                shoppingCartProvider
        );
    }

    @Test
    @DisplayName("""
            Clear shopping cart removes all items
            """)
    void clear_RemovesAllItemsFromShoppingCart() {
        ShoppingCart shoppingCart = createShoppingCart();

        when(shoppingCartProvider.getUserShoppingCart()).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(FIRST_ARGUMENT_INDEX));

        ArgumentCaptor<ShoppingCart> captor = ArgumentCaptor.forClass(ShoppingCart.class);

        shoppingCartService.clear();

        verify(shoppingCartRepository).save(captor.capture());
        ShoppingCart updatedShoppingCart = captor.getValue();

        assertEquals(EMPTY_SHOPPING_CART_SIZE, updatedShoppingCart.getCartItems().size());

        verify(shoppingCartProvider).getUserShoppingCart();

        verifyNoMoreInteractions(
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                shoppingCartMapper,
                shoppingCartProvider
        );
    }

    private ShoppingCart createShoppingCart() {
        ShoppingCart cart = new ShoppingCart();
        CartItem item = new CartItem();

        cart.setId(ID_EXAMPLE);
        cart.setUser(createUser(ID_EXAMPLE));

        item.setId(ID_EXAMPLE);
        item.setBook(createDefaultBook());
        item.setQuantity(DEFAULT_QUANTITY);

        item.setShoppingCart(cart);
        cart.setCartItems(new HashSet<>(Set.of(item)));

        return cart;
    }

    private ShoppingCartDto createShoppingCartDto() {
        CartItemDto cartItemDto = new CartItemDto()
                .setId(ID_EXAMPLE)
                .setBookTitle(FIRST_BOOK_TITLE_EXAMPLE)
                .setBookId(FIRST_BOOK_ID_EXAMPLE)
                .setQuantity(DEFAULT_QUANTITY);

        return new ShoppingCartDto()
                .setId(ID_EXAMPLE)
                .setUserId(ID_EXAMPLE)
                .setCartItems(Set.of(cartItemDto));
    }

    private ShoppingCartDto createUpdatedShoppingCartDtoWithSingleBook() {
        CartItemDto firstCartItemDto = new CartItemDto()
                .setId(ID_EXAMPLE)
                .setBookTitle(FIRST_BOOK_TITLE_EXAMPLE)
                .setBookId(FIRST_BOOK_ID_EXAMPLE)
                .setQuantity(INCREASED_QUANTITY);

        return new ShoppingCartDto()
                .setId(ID_EXAMPLE)
                .setUserId(ID_EXAMPLE)
                .setCartItems(Set.of(firstCartItemDto));
    }

    private ShoppingCartDto createUpdatedShoppingCartDtoWithTwoBooks() {
        CartItemDto firstCartItemDto = new CartItemDto()
                .setId(ID_EXAMPLE)
                .setBookTitle(FIRST_BOOK_TITLE_EXAMPLE)
                .setBookId(FIRST_BOOK_ID_EXAMPLE)
                .setQuantity(DEFAULT_QUANTITY);

        CartItemDto secondCartItemDto = new CartItemDto()
                .setId(SECOND_ID_EXAMPLE)
                .setBookTitle(SECOND_BOOK_TITLE_EXAMPLE)
                .setBookId(SECOND_BOOK_ID_EXAMPLE)
                .setQuantity(DEFAULT_QUANTITY);

        return new ShoppingCartDto()
                .setId(ID_EXAMPLE)
                .setUserId(ID_EXAMPLE)
                .setCartItems(Set.of(firstCartItemDto, secondCartItemDto));
    }

    private AddCartItemRequestDto createAddCartItemRequestDto(Long bookId, int quantity) {
        return new AddCartItemRequestDto()
                .setBookId(bookId)
                .setQuantity(quantity);
    }

    private UpdateCartItemRequestDto createUpdateCartItemRequestDto() {
        return new UpdateCartItemRequestDto().setQuantity(INCREASED_QUANTITY);
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail(USER_EMAIL_EXAMPLE);
        user.setPassword(USER_PASSWORD_EXAMPLE);
        user.setFirstName(USER_FIRST_NAME_EXAMPLE);
        user.setLastName(USER_LAST_NAME_EXAMPLE);
        user.setShippingAddress(USER_SHIPPING_ADDRESS_EXAMPLE);

        Role userRole = new Role();
        userRole.setId(USER_ROLE_ID);
        userRole.setName(ROLE_NAME_USER);

        user.setRoles(Set.of(userRole));
        user.setDeleted(false);

        return user;
    }

    private Book createBook(
            Long id,
            String title,
            String author,
            String isbn,
            BigDecimal price,
            String desc,
            String coverImage,
            Set<Category> categories
    ) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setDescription(desc);
        book.setCoverImage(coverImage);
        book.setCategories(categories);

        return book;
    }

    private Book createDefaultBook() {
        return createBook(
                FIRST_BOOK_ID_EXAMPLE,
                FIRST_BOOK_TITLE_EXAMPLE,
                FIRST_BOOK_AUTHOR_EXAMPLE,
                FIRST_BOOK_ISBN_EXAMPLE,
                FIRST_BOOK_PRICE_EXAMPLE,
                FIRST_BOOK_DESC_EXAMPLE,
                FIRST_BOOK_COVER_IMAGE_EXAMPLE,
                Set.of(new Category(FIRST_CATEGORY_ID_EXAMPLE))
        );
    }

    private Book createSecondBook() {
        return createBook(
                SECOND_BOOK_ID_EXAMPLE,
                SECOND_BOOK_TITLE_EXAMPLE,
                SECOND_BOOK_AUTHOR_EXAMPLE,
                SECOND_BOOK_ISBN_EXAMPLE,
                SECOND_BOOK_PRICE_EXAMPLE,
                SECOND_BOOK_DESC_EXAMPLE,
                SECOND_BOOK_COVER_IMAGE_EXAMPLE,
                Set.of(new Category(SECOND_CATEGORY_ID_EXAMPLE))
        );
    }
}
