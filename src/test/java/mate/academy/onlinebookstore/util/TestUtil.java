package mate.academy.onlinebookstore.util;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import mate.academy.onlinebookstore.dto.cartitem.AddCartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemDto;
import mate.academy.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.model.Role;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.model.enums.RoleName;

public class TestUtil {
    private static final Long FIRST_BOOK_ID_EXAMPLE = 1L;
    private static final String FIRST_BOOK_TITLE_EXAMPLE = "The Hobbit";
    private static final String USER_EMAIL_EXAMPLE = "testUser@i.ua";
    private static final int DEFAULT_QUANTITY = 1;
    private static final Long ID_EXAMPLE = 100L;
    private static final Long SECOND_CART_ITEM_ID_EXAMPLE = 101L;
    private static final String SECOND_BOOK_TITLE_EXAMPLE = "Spring in Action";
    private static final Long SECOND_BOOK_ID_EXAMPLE = 2L;
    private static final int INCREASED_QUANTITY = 2;
    private static final String NOT_FOUND_CART_ITEM_MESSAGE = "Can't find cart item by id: ";
    private static final String NOT_FOUND_BOOK_MESSAGE = "Can't find book by id: ";
    private static final Long SECOND_ID_EXAMPLE = 101L;
    private static final Long NON_EXISTING_ID_EXAMPLE = 999L;
    private static final int FIRST_ARGUMENT_INDEX = 0;
    private static final int EMPTY_SHOPPING_CART_SIZE = 0;
    private static final Long USER_ROLE_ID = 1L;
    private static final RoleName ROLE_NAME_USER = RoleName.USER;
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

    public static Book createBook(
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

    public static Book createDefaultBook() {
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

    public static Book createSecondBook() {
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

    public static User createUser() {
        User user = new User();
        user.setId(ID_EXAMPLE);
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

    public static ShoppingCart createShoppingCart() {
        ShoppingCart cart = new ShoppingCart();
        CartItem item = new CartItem();

        cart.setId(ID_EXAMPLE);
        cart.setUser(createUser());

        item.setId(ID_EXAMPLE);
        item.setBook(createDefaultBook());
        item.setQuantity(DEFAULT_QUANTITY);

        item.setShoppingCart(cart);
        cart.setCartItems(new HashSet<>(Set.of(item)));

        return cart;
    }

    public static ShoppingCartDto createShoppingCartDto() {
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

    public static ShoppingCartDto createUpdatedShoppingCartDtoWithSingleBook() {
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

    public static ShoppingCartDto createUpdatedShoppingCartDtoWithTwoBooks() {
        CartItemDto firstCartItemDto = new CartItemDto()
                .setId(ID_EXAMPLE)
                .setBookTitle(FIRST_BOOK_TITLE_EXAMPLE)
                .setBookId(FIRST_BOOK_ID_EXAMPLE)
                .setQuantity(DEFAULT_QUANTITY);

        CartItemDto secondCartItemDto = new CartItemDto()
                .setId(SECOND_CART_ITEM_ID_EXAMPLE)
                .setBookTitle(SECOND_BOOK_TITLE_EXAMPLE)
                .setBookId(SECOND_BOOK_ID_EXAMPLE)
                .setQuantity(DEFAULT_QUANTITY);

        return new ShoppingCartDto()
                .setId(ID_EXAMPLE)
                .setUserId(ID_EXAMPLE)
                .setCartItems(Set.of(firstCartItemDto, secondCartItemDto));
    }

    public static AddCartItemRequestDto createAddCartItemRequestDto(Long bookId) {
        return new AddCartItemRequestDto()
                .setBookId(bookId)
                .setQuantity(DEFAULT_QUANTITY);
    }

    public static UpdateCartItemRequestDto createUpdateCartItemRequestDto() {
        return new UpdateCartItemRequestDto().setQuantity(INCREASED_QUANTITY);
    }
}
