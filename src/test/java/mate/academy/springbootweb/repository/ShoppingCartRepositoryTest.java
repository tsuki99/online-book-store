package mate.academy.springbootweb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;
import mate.academy.springbootweb.model.CartItem;
import mate.academy.springbootweb.model.ShoppingCart;
import mate.academy.springbootweb.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    private static final Long SHOPPING_CART_ID_EXAMPLE = 100L;
    private static final Long BOOK_ID_EXAMPLE = 1L;
    private static final int QUANTITY_EXAMPLE = 1;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find shopping cart by user id returns optional of shopping cart")
    @Sql(
            scripts = {
                    "classpath:database/categories/add-two-categories-to-categories-table.sql",
                    "classpath:database/books/add-three-books-to-books-table.sql",
                    "classpath:database/books_categories/add-books-categories.sql",
                    "classpath:database/users/add-default-user.sql",
                    "classpath:database/shopping_carts/add-default-shopping-cart.sql",
                    "classpath:database/cart_items/add-default-cart-items.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/cart_items/remove-default-cart-items.sql",
                    "classpath:database/shopping_carts/remove-default-shopping-cart.sql",
                    "classpath:database/users/remove-default-user.sql",
                    "classpath:database/books_categories/remove-books-categories.sql",
                    "classpath:database/books/remove-all-books.sql",
                    "classpath:database/categories/remove-all-categories.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findByUserId_ExistingId_ReturnsOptionalShoppingCart() {
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(100L);

        assertTrue(actual.isPresent());

        ShoppingCart shoppingCart = actual.get();
        CartItem cartItem = shoppingCart.getCartItems().iterator().next();

        assertEquals(SHOPPING_CART_ID_EXAMPLE, shoppingCart.getId());
        assertEquals(SHOPPING_CART_ID_EXAMPLE, shoppingCart.getUser().getId());
        assertEquals(QUANTITY_EXAMPLE, shoppingCart.getCartItems().size());

        assertEquals(BOOK_ID_EXAMPLE, cartItem.getBook().getId());
        assertEquals(QUANTITY_EXAMPLE, cartItem.getQuantity());
    }
}
