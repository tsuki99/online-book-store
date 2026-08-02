package mate.academy.springbootweb.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import mate.academy.springbootweb.dto.book.BookDto;
import mate.academy.springbootweb.dto.book.CreateBookRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
                "classpath:database/books_categories/add-books-categories.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD

)
@Sql(
        scripts = {
               "classpath:database/books_categories/remove-books-categories.sql",
                "classpath:database/books/remove-all-books.sql",
                "classpath:database/categories/remove-all-categories.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public class BookControllerTest {
    private static final int DEFAULT_PAGE_START_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 6;
    private static final int SINGLE_PAGE_TOTAL_ELEMENTS = 1;
    private static final int DEFAULT_TOTAL_PAGES_COUNT = 1;
    private static final BigDecimal PRICE_EXAMPLE = new BigDecimal("500.00");
    private static final String TITLE_EXAMPLE = "The Hobbit";
    private static final String AUTHOR_EXAMPLE = "J.R.R. Tolkien";
    private static final String ISBN_EXAMPLE = "978000000001";
    private static final String DESCRIPTION_EXAMPLE = "Fantasy novel";
    private static final String COVER_IMAGE_EXAMPLE = "hobbit.jpg";
    private static final String TITLE_PREFIX = "The";
    private static final Long ID_EXAMPLE = 1L;
    private static final Long SECOND_BOOK_ID = 2L;
    private static final Long THIRD_BOOK_ID = 3L;
    private static final Long NEW_BOOK_ID_EXAMPLE = 4L;
    private static final Set<Long> CATEGORY_IDS_EXAMPLE = Set.of(ID_EXAMPLE);
    private static final String DEFAULT_BOOK_REQUEST_TITLE = "Effective Java";
    private static final String DEFAULT_BOOK_REQUEST_AUTHOR = "Joshua Bloch";
    private static final String DEFAULT_BOOK_REQUEST_ISBN = "9780134685991";
    private static final BigDecimal DEFAULT_BOOK_REQUEST_PRICE = new BigDecimal("1200.00");
    private static final String DEFAULT_BOOK_REQUEST_DESCRIPTION = "Best practices for Java programming";
    private static final String DEFAULT_BOOK_REQUEST_COVER_IMAGE = "effective-java.jpg";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("""
            Get all books returns page DTO
            """)
    @WithMockUser(username = "user", authorities = {"USER"})
    void getAll_WithPageable_ReturnsPageDto() throws Exception {
        PageDto<BookDto> expectedPageDto = createAllBooksPageDto();

        MvcResult result = mockMvc.perform(
                        get("/books")
                                .param("page", "0")
                                .param("size", "6")
                )
                .andExpect(status().isOk())
                .andReturn();

        PageDto<BookDto> actualPageDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageDto<BookDto>>(){}
        );

        assertEquals(expectedPageDto, actualPageDto);
    }

    @Test
    @DisplayName("""
            Get book by existing id returns book DTO
            """)
    @WithMockUser(username = "user", authorities = {"USER"})
    void getById_WithExistingId_ReturnsBookDto() throws Exception {
        BookDto expectedBookDto = createDefaultBookDto();

        MvcResult result = mockMvc.perform(
                get("/books/{id}", ID_EXAMPLE)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actualBookDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );

        assertEquals(expectedBookDto, actualBookDto);
    }

    @Test
    @DisplayName("""
            Delete existing book returns NO CONTENT status
            """)
    @WithMockUser(username = "admin", authorities = {"ADMIN", "USER"})
    void deleteById_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(
                delete("/books/{id}", ID_EXAMPLE)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                get("/books/{id}", ID_EXAMPLE)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Save valid book request returns created book DTO
            """)
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void save_WithValidBookRequestDto_ReturnsBookDto() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        BookDto expectedBookDto = createBookDto(
                NEW_BOOK_ID_EXAMPLE,
                DEFAULT_BOOK_REQUEST_TITLE,
                DEFAULT_BOOK_REQUEST_AUTHOR,
                DEFAULT_BOOK_REQUEST_ISBN,
                DEFAULT_BOOK_REQUEST_PRICE,
                DEFAULT_BOOK_REQUEST_DESCRIPTION,
                DEFAULT_BOOK_REQUEST_COVER_IMAGE,
                CATEGORY_IDS_EXAMPLE
        );

        MvcResult result = mockMvc.perform(
                post("/books")
                        .content(objectMapper.writeValueAsString(bookRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actualBookDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );

        assertEquals(expectedBookDto, actualBookDto);
    }

    @Test
    @DisplayName("""
            Update existing book returns updated book DTO
            """)
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateById_WithExistingIdAndValidBookRequestDto_ReturnsBookDto() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        BookDto expectedBookDto = createBookDto(
                ID_EXAMPLE,
                DEFAULT_BOOK_REQUEST_TITLE,
                DEFAULT_BOOK_REQUEST_AUTHOR,
                DEFAULT_BOOK_REQUEST_ISBN,
                DEFAULT_BOOK_REQUEST_PRICE,
                DEFAULT_BOOK_REQUEST_DESCRIPTION,
                DEFAULT_BOOK_REQUEST_COVER_IMAGE,
                CATEGORY_IDS_EXAMPLE
        );

        MvcResult result = mockMvc.perform(
                        put("/books/{id}", ID_EXAMPLE)
                                .content(objectMapper.writeValueAsString(bookRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actualBookDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );

        assertEquals(expectedBookDto, actualBookDto);
    }

    @Test
    @DisplayName("""
            Search books with valid parameters returns page of books
            """)
    @WithMockUser(username = "user", authorities = {"USER"})
    void search_WithValidSearchParameters_ReturnsPageDto() throws Exception {
        PageDto<BookDto> expectedPageDto = createSingleBookPageDto();

        MvcResult result = mockMvc.perform(
                get("/books/search")
                        .param("titleParts", TITLE_PREFIX)
                        .param("page", "0")
                        .param("size", "6")
                )
                .andExpect(status().isOk())
                .andReturn();

        PageDto<BookDto> actualPageDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageDto<BookDto>>(){}
        );

        assertEquals(expectedPageDto, actualPageDto);
    }

    private PageDto<BookDto> createSingleBookPageDto() {
        return new PageDto<>(
                List.of(createDefaultBookDto()),
                DEFAULT_PAGE_START_NUMBER,
                DEFAULT_PAGE_SIZE,
                SINGLE_PAGE_TOTAL_ELEMENTS,
                DEFAULT_TOTAL_PAGES_COUNT
        );
    }

    private PageDto<BookDto> createAllBooksPageDto() {
        List<BookDto> booksList = List.of(
                createBookDto(
                        THIRD_BOOK_ID,
                        "Clean Code",
                        "Robert C. Martin",
                        "978000000003",
                        new BigDecimal("800.00"),
                        "Programming practices",
                        "clean-code.jpg",
                        Set.of(2L)
                ),
                createBookDto(
                        SECOND_BOOK_ID,
                        "Spring in Action",
                        "Craig Walls",
                        "978000000002",
                        new BigDecimal("900.00"),
                        "Spring Framework",
                        "spring.jpg",
                        Set.of(2L)),
                createDefaultBookDto()
        );

        return new PageDto<>(
                booksList,
                DEFAULT_PAGE_START_NUMBER,
                DEFAULT_PAGE_SIZE,
                booksList.size(),
                DEFAULT_TOTAL_PAGES_COUNT
        );
    }

    private BookDto createBookDto(
            Long id,
            String title,
            String author,
            String isbn,
            BigDecimal price,
            String description,
            String coverImage,
            Set<Long> categoryIds
    ) {
        return new BookDto()
                .setId(id)
                .setTitle(title)
                .setAuthor(author)
                .setIsbn(isbn)
                .setPrice(price)
                .setDescription(description)
                .setCoverImage(coverImage)
                .setCategoryIds(categoryIds);
    }

    private BookDto createDefaultBookDto() {
        return createBookDto(
                ID_EXAMPLE,
                TITLE_EXAMPLE,
                AUTHOR_EXAMPLE,
                ISBN_EXAMPLE,
                PRICE_EXAMPLE,
                DESCRIPTION_EXAMPLE,
                COVER_IMAGE_EXAMPLE,
                CATEGORY_IDS_EXAMPLE
        );
    }

    private CreateBookRequestDto createBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle(DEFAULT_BOOK_REQUEST_TITLE)
                .setAuthor(DEFAULT_BOOK_REQUEST_AUTHOR)
                .setIsbn(DEFAULT_BOOK_REQUEST_ISBN)
                .setPrice(DEFAULT_BOOK_REQUEST_PRICE)
                .setDescription(DEFAULT_BOOK_REQUEST_DESCRIPTION)
                .setCoverImage(DEFAULT_BOOK_REQUEST_COVER_IMAGE)
                .setCategoryIds(CATEGORY_IDS_EXAMPLE);
    }
}
