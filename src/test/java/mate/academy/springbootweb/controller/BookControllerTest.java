package mate.academy.springbootweb.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import mate.academy.springbootweb.dto.book.BookDto;
import mate.academy.springbootweb.dto.book.BookSearchParameters;
import mate.academy.springbootweb.dto.book.CreateBookRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.security.JwtAuthenticationFilter;
import mate.academy.springbootweb.service.book.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {
    private static final PageRequest DEFAULT_PAGEABLE = PageRequest.of(
            0,
            6,
            Sort.by("title").ascending()
    );
    private static final BigDecimal PRICE_EXAMPLE = BigDecimal.valueOf(500);
    private static final String TITLE_EXAMPLE = "The Hobbit";
    private static final String AUTHOR_EXAMPLE = "J.R.R. Tolkien";
    private static final String ISBN_EXAMPLE = "978000000001";
    private static final String DESCRIPTION_EXAMPLE = "Fantasy novel";
    private static final String COVER_IMAGE_EXAMPLE = "hobbit.jpg";
    private static final String TITLE_PREFIX = "The";
    private static final Long ID_EXAMPLE = 1L;
    private static final Set<Long> CATEGORY_IDS_EXAMPLE = Set.of(ID_EXAMPLE);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("""
            Get all books returns page DTO
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getAll_WithPageable_ReturnsPageDto() throws Exception {
        PageDto<BookDto> expectedPageDto = createPageDto();

        when(bookService.findAll(any())).thenReturn(expectedPageDto);

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

        verify(bookService).findAll(DEFAULT_PAGEABLE);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    @DisplayName("""
            Get book by existing id returns book DTO
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getById_WithExistingId_ReturnsBookDto() throws Exception {
        BookDto expectedBookDto = createBookDto();

        when(bookService.findBookById(ID_EXAMPLE)).thenReturn(expectedBookDto);

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
        verify(bookService).findBookById(ID_EXAMPLE);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    @DisplayName("""
            Delete existing book returns NO CONTENT status
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteById_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(
                delete("/books/{id}", ID_EXAMPLE)
                )
                .andExpect(status().isNoContent());

        verify(bookService).deleteById(ID_EXAMPLE);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    @DisplayName("""
            Save valid book request returns created book DTO
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void save_WithValidBookRequestDto_ReturnsBookDto() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        BookDto expectedBookDto = createBookDto();

        when(bookService.save(bookRequestDto)).thenReturn(expectedBookDto);

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
        verify(bookService).save(bookRequestDto);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    @DisplayName("""
            Update existing book returns updated book DTO
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateById_WithExistingIdAndValidBookRequestDto_ReturnsBookDto() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        BookDto expectedBookDto = createBookDto();

        when(bookService.updateById(ID_EXAMPLE, bookRequestDto)).thenReturn(expectedBookDto);

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
        verify(bookService).updateById(ID_EXAMPLE, bookRequestDto);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    @DisplayName("""
            Search books with valid parameters returns page of books
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void search_WithValidSearchParameters_ReturnsPageDto() throws Exception {
        PageDto<BookDto> expectedPageDto = createPageDto();
        BookSearchParameters expectedParameters = createBookSearchParameters();

        when(bookService.search(any(), any())).thenReturn(expectedPageDto);

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

        ArgumentCaptor<BookSearchParameters> bookSearchParametersCaptor =
                ArgumentCaptor.forClass(BookSearchParameters.class);

        assertEquals(expectedPageDto, actualPageDto);

        verify(bookService).search(
                bookSearchParametersCaptor.capture(),
                eq(DEFAULT_PAGEABLE)
        );
        verifyNoMoreInteractions(bookService);

        BookSearchParameters actualParameters = bookSearchParametersCaptor.getValue();

        assertArrayEquals(expectedParameters.titleParts(), actualParameters.titleParts());
        assertArrayEquals(expectedParameters.authors(), actualParameters.authors());
        assertArrayEquals(expectedParameters.isbns(), actualParameters.isbns());
    }

    private BookSearchParameters createBookSearchParameters() {
        return new BookSearchParameters(
                new String[]{TITLE_PREFIX},
                null,
                null
        );
    }

    private PageDto<BookDto> createPageDto() {
        return new PageDto<>(
                List.of(createBookDto()),
                0,
                6,
                1,
                1
        );
    }

    private BookDto createBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(ID_EXAMPLE);
        bookDto.setTitle(TITLE_EXAMPLE);
        bookDto.setAuthor(AUTHOR_EXAMPLE);
        bookDto.setIsbn(ISBN_EXAMPLE);
        bookDto.setPrice(PRICE_EXAMPLE);
        bookDto.setDescription(DESCRIPTION_EXAMPLE);
        bookDto.setCoverImage(COVER_IMAGE_EXAMPLE);
        bookDto.setCategoryIds(CATEGORY_IDS_EXAMPLE);

        return bookDto;
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setTitle(TITLE_EXAMPLE);
        bookRequestDto.setAuthor(AUTHOR_EXAMPLE);
        bookRequestDto.setIsbn(ISBN_EXAMPLE);
        bookRequestDto.setPrice(PRICE_EXAMPLE);
        bookRequestDto.setDescription(DESCRIPTION_EXAMPLE);
        bookRequestDto.setCoverImage(COVER_IMAGE_EXAMPLE);
        bookRequestDto.setCategoryIds(CATEGORY_IDS_EXAMPLE);

        return bookRequestDto;
    }
}
