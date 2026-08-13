package mate.academy.onlinebookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.onlinebookstore.dto.cartitem.AddCartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.security.JwtAuthenticationFilter;
import mate.academy.onlinebookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Sql(
        scripts = {
                "classpath:database/categories/add-two-categories-to-categories-table.sql",
                "classpath:database/books/add-three-books-to-books-table.sql",
                "classpath:database/books_categories/add-books-categories.sql",
                "classpath:database/users/add-default-user.sql",
                "classpath:database/users_roles/add-default-user-role.sql",
                "classpath:database/shopping_carts/add-default-shopping-cart.sql",
                "classpath:database/cart_items/add-default-cart-items.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = {
                "classpath:database/cart_items/remove-default-cart-items.sql",
                "classpath:database/shopping_carts/remove-default-shopping-cart.sql",
                "classpath:database/users_roles/remove-default-user-role.sql",
                "classpath:database/users/remove-default-user.sql",
                "classpath:database/books_categories/remove-books-categories.sql",
                "classpath:database/books/remove-all-books.sql",
                "classpath:database/categories/remove-all-categories.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public class ShoppingCartControllerTest {
        private static final String USER_EMAIL_EXAMPLE = "testUser@i.ua";
        private static final Long ID_EXAMPLE = 100L;
        private static final Long SECOND_BOOK_ID_EXAMPLE = 2L;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Test
        @DisplayName("""
                Get user's shopping cart returns shopping cart DTO
                """)
        @WithUserDetails(USER_EMAIL_EXAMPLE)
        void getUserShoppingCart_WithExistingShoppingCart_ReturnsShoppingCartDto()
                throws Exception {

                ShoppingCartDto expectedShoppingCartDto = TestUtil.createShoppingCartDto();

                MvcResult result = mockMvc.perform(
                        get("/cart"))
                        .andExpect(status().isOk())
                        .andReturn();

                ShoppingCartDto actualShoppingCartDto = objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ShoppingCartDto.class
                );

                assertEquals(expectedShoppingCartDto, actualShoppingCartDto);
        }

        @Test
        @DisplayName("""
                Add book to shopping cart returns updated shopping cart DTO
                """)
        @WithUserDetails(USER_EMAIL_EXAMPLE)
        void addBookToShoppingCart_WithValidRequestDto_ReturnsShoppingCartDto() throws Exception {
                AddCartItemRequestDto addCartItemRequestDto =
                        TestUtil.createAddCartItemRequestDto(SECOND_BOOK_ID_EXAMPLE);

                ShoppingCartDto expectedShoppingCartDto =
                        TestUtil.createUpdatedShoppingCartDtoWithTwoBooks();

                MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(objectMapper.writeValueAsString(addCartItemRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isCreated())
                        .andReturn();

                ShoppingCartDto actualShoppingCartDto = objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ShoppingCartDto.class
                );

                assertEquals(expectedShoppingCartDto, actualShoppingCartDto);
        }

        @Test
        @DisplayName("""
                Update cart item quantity returns shopping cart DTO
                """)
        @WithUserDetails(USER_EMAIL_EXAMPLE)
        void updateBookQuantityByBookId_WithValidRequestDto_ReturnsShoppingCartDto()
                throws Exception {

                UpdateCartItemRequestDto updateCartItemRequestDto =
                        TestUtil.createUpdateCartItemRequestDto();

                ShoppingCartDto expectedShoppingCartDto =
                        TestUtil.createUpdatedShoppingCartDtoWithSingleBook();

                MvcResult result = mockMvc.perform(
                        put("/cart/items/{id}", ID_EXAMPLE)
                                .content(objectMapper.writeValueAsString(updateCartItemRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                        .andExpect(status().isOk())
                        .andReturn();

                ShoppingCartDto actualShoppingCartDto = objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ShoppingCartDto.class
                );

                assertEquals(expectedShoppingCartDto, actualShoppingCartDto);
        }

        @Test
        @DisplayName("""
                Remove existing book from shopping cart returns NO CONTENT status
                """)
        @WithUserDetails(USER_EMAIL_EXAMPLE)
        void removeBookFromShoppingCart_WithExistingId_ReturnsNoContent() throws Exception {
                mockMvc.perform(
                        delete("/cart/items/{id}", ID_EXAMPLE)
                )
                        .andExpect(status().isNoContent());

                MvcResult result = mockMvc.perform(
                        get("/cart")
                )
                        .andExpect(status().isOk())
                        .andReturn();

                ShoppingCartDto actualShoppingCartDto = objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ShoppingCartDto.class
                );

                assertTrue(actualShoppingCartDto.getCartItems().isEmpty());
        }
}
