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
import mate.academy.springbootweb.service.book.BookService;
import mate.academy.springbootweb.service.category.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {
    private static final PageRequest CATEGORY_DEFAULT_PAGEABLE = PageRequest.of(
            0,
            6,
            Sort.by("name").ascending()
    );
    private static final PageRequest BOOK_DEFAULT_PAGEABLE = PageRequest.of(
            0,
            6,
            Sort.by("title").ascending()
    );
    private static final BigDecimal BOOK_PRICE_EXAMPLE = BigDecimal.valueOf(500);
    private static final Long ID_EXAMPLE = 1L;
    private static final String CATEGORY_NAME_EXAMPLE = "Fantasy";
    private static final String CATEGORY_DESC_EXAMPLE = "Fantasy worlds, magic and adventures";
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
    private CategoryService categoryService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("""
            Save valid category request returns created category DTO
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_WithValidCategoryRequestDto_ReturnsCategoryDto() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        CategoryDto expectedCategoryDto = createCategoryDto();

        when(categoryService.save(categoryRequestDto)).thenReturn(expectedCategoryDto);

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

        verify(categoryService).save(categoryRequestDto);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @DisplayName("""
            Get all categories returns page DTO
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getAll_WithPageable_ReturnsPageDto() throws Exception {
        PageDto<CategoryDto> expectedPageDto = createCategoryDtoPageDto();

        when(categoryService.findAll(any())).thenReturn(expectedPageDto);

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

        verify(categoryService).findAll(CATEGORY_DEFAULT_PAGEABLE);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @DisplayName("""
            Get category by existing id returns category DTO
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getCategoryById_WithExistingId_ReturnsCategoryDto() throws Exception {
        CategoryDto expectedCategoryDto = createCategoryDto();

        when(categoryService.findById(ID_EXAMPLE)).thenReturn(expectedCategoryDto);

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

        verify(categoryService).findById(ID_EXAMPLE);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @DisplayName("""
            Update existing category by id returns category DTO
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategory_WithExistingId_ReturnsCategoryDto()
            throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        CategoryDto expectedCategoryDto = createCategoryDto();

        when(categoryService.update(
                ID_EXAMPLE,
                categoryRequestDto)
        )
                .thenReturn(expectedCategoryDto);

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

        verify(categoryService).update(ID_EXAMPLE, categoryRequestDto);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @DisplayName("""
            Delete existing category by id returns NO CONTENT status
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategory_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(
                delete("/categories/{id}", ID_EXAMPLE)
                )
                .andExpect(status().isNoContent());

        verify(categoryService).deleteById(ID_EXAMPLE);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @DisplayName("""
            Get books by existing category id returns page DTO
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getBooksByCategoryId_WithExistingId_ReturnsPageDto() throws Exception {
        PageDto<BookDtoWithoutCategoryIds> expectedPageDto = createBookDtoPageDto();

        when(bookService.findBooksByCategoryId(
                eq(ID_EXAMPLE),
                any(Pageable.class))
        )
                .thenReturn(expectedPageDto);

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

        verify(bookService).findBooksByCategoryId(ID_EXAMPLE, BOOK_DEFAULT_PAGEABLE);
        verifyNoMoreInteractions(bookService);
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName(CATEGORY_NAME_EXAMPLE)
                .setDescription(CATEGORY_DESC_EXAMPLE);
    }

    private CategoryDto createCategoryDto() {
        return new CategoryDto()
                .setId(ID_EXAMPLE)
                .setName(CATEGORY_NAME_EXAMPLE)
                .setDescription(CATEGORY_DESC_EXAMPLE);
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds() {
        return new BookDtoWithoutCategoryIds()
                .setId(ID_EXAMPLE)
                .setTitle(BOOK_TITLE_EXAMPLE)
                .setAuthor(BOOK_AUTHOR_EXAMPLE)
                .setIsbn(BOOK_ISBN_EXAMPLE)
                .setPrice(BOOK_PRICE_EXAMPLE)
                .setDescription(BOOK_DESC_EXAMPLE)
                .setCoverImage(BOOK_COVER_IMAGE_EXAMPLE);
    }

    private PageDto<CategoryDto> createCategoryDtoPageDto() {
        return new PageDto<>(
                List.of(createCategoryDto()),
                0,
                6,
                1,
                1
        );
    }

    private PageDto<BookDtoWithoutCategoryIds> createBookDtoPageDto() {
        return new PageDto<>(
                List.of(createBookDtoWithoutCategoryIds()),
                0,
                6,
                1,
                1
        );
    }
}
