package mate.academy.springbootweb.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.springbootweb.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbootweb.dto.category.CategoryDto;
import mate.academy.springbootweb.dto.category.CreateCategoryRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
public class CategoryControllerTest {
    private static final int DEFAULT_PAGE_START_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 6;
    private static final int SINGLE_PAGE_TOTAL_ELEMENTS = 1;
    private static final int DEFAULT_TOTAL_PAGES_COUNT = 1;
    private static final BigDecimal BOOK_PRICE_EXAMPLE = new BigDecimal("500.00");
    private static final Long ID_EXAMPLE = 1L;
    private static final Long NEW_CATEGORY_ID_EXAMPLE = 3L;
    private static final String CATEGORY_NAME_EXAMPLE = "Fantasy";
    private static final String CATEGORY_DESC_EXAMPLE = "Fantasy books";
    private static final String DEFAULT_CATEGORY_REQUEST_NAME = "Science Fiction";
    private static final String DEFAULT_CATEGORY_REQUEST_DESCRIPTION =
            "Books about futuristic technologies and space exploration";
    private static final String BOOK_TITLE_EXAMPLE = "The Hobbit";
    private static final String BOOK_AUTHOR_EXAMPLE = "J.R.R. Tolkien";
    private static final String BOOK_ISBN_EXAMPLE = "978000000001";
    private static final String BOOK_DESC_EXAMPLE = "Fantasy novel";
    private static final String BOOK_COVER_IMAGE_EXAMPLE = "hobbit.jpg";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("""
            Save valid category request returns created category DTO
            """)
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCategory_WithValidCategoryRequestDto_ReturnsCategoryDto() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        CategoryDto expectedCategoryDto = createCategoryDto(
                NEW_CATEGORY_ID_EXAMPLE,
                DEFAULT_CATEGORY_REQUEST_NAME,
                DEFAULT_CATEGORY_REQUEST_DESCRIPTION
        );


        MvcResult result = mockMvc.perform(
                post("/categories")
                        .content(objectMapper.writeValueAsString(categoryRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actualCategoryDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertEquals(expectedCategoryDto, actualCategoryDto);
    }

    @Test
    @DisplayName("""
            Get all categories returns page DTO
            """)
    @WithMockUser(username = "user", authorities = {"USER"})
    void getAll_WithPageable_ReturnsPageDto() throws Exception {
        PageDto<CategoryDto> expectedPageDto = createAllCategoriesPageDto();

        MvcResult result = mockMvc.perform(
                get("/categories")
                        .param("page", "0")
                        .param("size", "6")
        )
                .andExpect(status().isOk())
                .andReturn();

        PageDto<CategoryDto> actualPageDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageDto<CategoryDto>>() {}
        );

        assertEquals(expectedPageDto, actualPageDto);
    }

    @Test
    @DisplayName("""
            Get category by existing id returns category DTO
            """)
    @WithMockUser(username = "user", authorities = {"USER"})
    void getCategoryById_WithExistingId_ReturnsCategoryDto() throws Exception {
        CategoryDto expectedCategoryDto = createDefaultCategoryDto();

        MvcResult result = mockMvc.perform(
                get("/categories/{id}", ID_EXAMPLE)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actualCategoryDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertEquals(expectedCategoryDto, actualCategoryDto);
    }

    @Test
    @DisplayName("""
            Update existing category by id returns category DTO
            """)
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateCategory_WithExistingId_ReturnsCategoryDto()
            throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        CategoryDto expectedCategoryDto = createCategoryDto(
                ID_EXAMPLE,
                DEFAULT_CATEGORY_REQUEST_NAME,
                DEFAULT_CATEGORY_REQUEST_DESCRIPTION
        );

        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", ID_EXAMPLE)
                                .content(objectMapper.writeValueAsString(categoryRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actualCategoryDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertEquals(expectedCategoryDto, actualCategoryDto);
    }

    @Test
    @DisplayName("""
            Delete existing category by id returns NO CONTENT status
            """)
    @WithMockUser(username = "admin", authorities = {"ADMIN", "USER"})
    void deleteCategory_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(
                delete("/categories/{id}", ID_EXAMPLE)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                get("/categories/{id}", ID_EXAMPLE)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Get books by existing category id returns page DTO
            """)
    @WithMockUser(username = "user", authorities = {"USER"})
    void getBooksByCategoryId_WithExistingId_ReturnsPageDto() throws Exception {
        PageDto<BookDtoWithoutCategoryIds> expectedPageDto = createBookDtoPageDto();

        MvcResult result = mockMvc.perform(
                get("/categories/{id}/books", ID_EXAMPLE)
                        .param("page", "0")
                        .param("size", "6")
        )
                .andExpect(status().isOk())
                .andReturn();

        PageDto<BookDtoWithoutCategoryIds> actualPageDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageDto<BookDtoWithoutCategoryIds>>(){}
        );

        assertEquals(expectedPageDto, actualPageDto);
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName(DEFAULT_CATEGORY_REQUEST_NAME)
                .setDescription(DEFAULT_CATEGORY_REQUEST_DESCRIPTION);
    }

    private CategoryDto createCategoryDto(
            Long id,
            String name,
            String description
    ) {
        return new CategoryDto()
                .setId(id)
                .setName(name)
                .setDescription(description);
    }

    private CategoryDto createDefaultCategoryDto() {
        return createCategoryDto(ID_EXAMPLE, CATEGORY_NAME_EXAMPLE, CATEGORY_DESC_EXAMPLE);
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds(
            Long id,
            String title,
            String author,
            String isbn,
            BigDecimal price,
            String description,
            String coverImage
    ) {
        return new BookDtoWithoutCategoryIds()
                .setId(id)
                .setTitle(title)
                .setAuthor(author)
                .setIsbn(isbn)
                .setPrice(price)
                .setDescription(description)
                .setCoverImage(coverImage);
    }

    private BookDtoWithoutCategoryIds createDefaultBookWithoutCategoryIds() {
        return createBookDtoWithoutCategoryIds(
                ID_EXAMPLE,
                BOOK_TITLE_EXAMPLE,
                BOOK_AUTHOR_EXAMPLE,
                BOOK_ISBN_EXAMPLE,
                BOOK_PRICE_EXAMPLE,
                BOOK_DESC_EXAMPLE,
                BOOK_COVER_IMAGE_EXAMPLE
        );
    }

    private PageDto<CategoryDto> createAllCategoriesPageDto() {
        List<CategoryDto> categoriesList = List.of(
                createDefaultCategoryDto(),
                createCategoryDto(
                        2L,
                        "Programming",
                        "Programming books"
                )
        );

        return new PageDto<>(
                categoriesList,
                DEFAULT_PAGE_START_NUMBER,
                DEFAULT_PAGE_SIZE,
                categoriesList.size(),
                DEFAULT_TOTAL_PAGES_COUNT
        );
    }

    private PageDto<BookDtoWithoutCategoryIds> createBookDtoPageDto() {
        return new PageDto<>(
                List.of(createDefaultBookWithoutCategoryIds()),
                DEFAULT_PAGE_START_NUMBER,
                DEFAULT_PAGE_SIZE,
                SINGLE_PAGE_TOTAL_ELEMENTS,
                DEFAULT_TOTAL_PAGES_COUNT
        );
    }
}
