package mate.academy.onlinebookstore.service;

import java.util.Optional;
import mate.academy.onlinebookstore.dto.cartitem.AddCartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.cartitem.CartItemRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartProvider;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartServiceImpl;
import mate.academy.onlinebookstore.util.TestUtil;
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
    private static final Long ID_EXAMPLE = 100L;
    private static final Long NON_EXISTING_ID_EXAMPLE = 999L;
    private static final Long FIRST_BOOK_ID_EXAMPLE = 1L;
    private static final Long SECOND_BOOK_ID_EXAMPLE = 2L;
    private static final int FIRST_ARGUMENT_INDEX = 0;
    private static final int EMPTY_SHOPPING_CART_SIZE = 0;
    private static final int DEFAULT_QUANTITY = 1;
    private static final int INCREASED_QUANTITY = 2;

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
        User existingUser = TestUtil.createUser();
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
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        ShoppingCartDto expectedShoppingCartDto = TestUtil.createShoppingCartDto();

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
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        Book book = TestUtil.createSecondBook();
        AddCartItemRequestDto addCartItemRequestDto =
                TestUtil.createAddCartItemRequestDto(SECOND_BOOK_ID_EXAMPLE);
        ShoppingCartDto expectedShoppingCartDto =
                TestUtil.createUpdatedShoppingCartDtoWithTwoBooks();

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
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        AddCartItemRequestDto addCartItemRequestDto =
                TestUtil.createAddCartItemRequestDto(FIRST_BOOK_ID_EXAMPLE);
        ShoppingCartDto expectedShoppingCartDto =
                TestUtil.createUpdatedShoppingCartDtoWithSingleBook();

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
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        AddCartItemRequestDto addCartItemRequestDto =
                TestUtil.createAddCartItemRequestDto(NON_EXISTING_ID_EXAMPLE);

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
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        Optional<CartItem> optionalCartItem = shoppingCart.getCartItems().stream().findFirst();
        ShoppingCartDto expectedShoppingCartDto =
                TestUtil.createUpdatedShoppingCartDtoWithSingleBook();

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
                TestUtil.createUpdateCartItemRequestDto()
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
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
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
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();

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
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();

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
}
