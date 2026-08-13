package mate.academy.onlinebookstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.onlinebookstore.dto.book.BookSearchParameters;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.dto.page.PageDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.mapper.page.PageMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.SpecificationBuilder;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.service.book.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final PageRequest DEFAULT_PAGEABLE = PageRequest.of(0, 6);
    private static final Long ID_EXAMPLE = 1L;
    private static final Long NON_EXISTING_ID_EXAMPLE = 999L;
    private static final String TITLE_PREFIX = "The";
    private static final String EXPECTED_EXCEPTION_MESSAGE = "Can't find book by id: ";
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private SpecificationBuilder<Book, BookSearchParameters> bookSpecificationBuilder;
    @Mock
    private PageMapper<BookDtoWithoutCategoryIds> pageMapperWithoutCategoryIds;
    @Mock
    private PageMapper<BookDto> pageMapper;
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(
                bookRepository,
                bookMapper,
                pageMapper,
                pageMapperWithoutCategoryIds,
                bookSpecificationBuilder
        );
    }

    @Test
    @DisplayName("""
            Find books by existing category id returns page dto
            """)
    void findBooksByCategoryId_WithExistingCategoryId_ReturnsPageDto() {
        Book book = createBook();
        BookDtoWithoutCategoryIds bookDto = createBookDtoWithoutCategories(book);
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        Page<BookDtoWithoutCategoryIds> bookDtoPage =
                new PageImpl<>(List.of(bookDto));

        when(bookRepository.findAllByCategoryId(ID_EXAMPLE, DEFAULT_PAGEABLE))
                .thenReturn(bookPage);

        when(bookMapper.toDtoWithoutCategories(book)).
                thenReturn(bookDto);

        PageDto<BookDtoWithoutCategoryIds> expectedPageDto =
                createPageDtoWithoutCategories(bookDtoPage);

        when(pageMapperWithoutCategoryIds.toDto(bookDtoPage))
                .thenReturn(expectedPageDto);

        PageDto<BookDtoWithoutCategoryIds> actualPageDto = bookService.findBooksByCategoryId(
                ID_EXAMPLE,
                DEFAULT_PAGEABLE
        );

        assertEquals(expectedPageDto, actualPageDto);

        verify(bookRepository).findAllByCategoryId(ID_EXAMPLE, DEFAULT_PAGEABLE);
        verify(bookMapper).toDtoWithoutCategories(book);
        verify(pageMapperWithoutCategoryIds).toDto(bookDtoPage);

        verifyNoMoreInteractions(
                bookRepository,
                bookMapper,
                pageMapper,
                pageMapperWithoutCategoryIds,
                bookSpecificationBuilder
        );
    }

    @Test
    @DisplayName("""
            Search books by valid parameters returns page dto
            """)
    void search_WithValidSearchParameters_ReturnsPageDto() {
        BookSearchParameters bookSearchParameters = createBookSearchParameters();

        Book book = createBook();
        BookDto bookDto = createBookDto(book);
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        Page<BookDto> bookDtoPage = new PageImpl<>(List.of(bookDto));
        Specification<Book> bookSpecification =
                (root, query, cb) -> null;

        when(bookSpecificationBuilder.build(bookSearchParameters))
                .thenReturn(bookSpecification);

        when(bookRepository.findAll(bookSpecification, DEFAULT_PAGEABLE))
                .thenReturn(bookPage);

        when(bookMapper.toDto(book)).thenReturn(bookDto);

        PageDto<BookDto> expectedPageDto = createPageDto(bookDtoPage);

        when(pageMapper.toDto(any()))
                .thenReturn(expectedPageDto);

        PageDto<BookDto> actualPageDto = bookService.search(bookSearchParameters, DEFAULT_PAGEABLE);

        assertEquals(expectedPageDto, actualPageDto);

        verify(bookSpecificationBuilder).build(bookSearchParameters);
        verify(bookRepository).findAll(bookSpecification, DEFAULT_PAGEABLE);
        verify(bookMapper).toDto(book);
        verify(pageMapper).toDto(bookDtoPage);

        verifyNoMoreInteractions(
                bookRepository,
                bookMapper,
                pageMapper,
                pageMapperWithoutCategoryIds,
                bookSpecificationBuilder
        );
    }

    @Test
    @DisplayName("""
            Update book with non-existing id throws exception
            """)
    void updateById_WithNotExistingId_ThrowsException() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();

        when(bookRepository.findById(NON_EXISTING_ID_EXAMPLE)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateById(NON_EXISTING_ID_EXAMPLE, requestDto)
        );

        assertEquals(
                EXPECTED_EXCEPTION_MESSAGE + NON_EXISTING_ID_EXAMPLE,
                exception.getMessage()
        );

        verify(bookRepository).findById(NON_EXISTING_ID_EXAMPLE);
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).updateBookFromDto(
                any(Book.class),
                any(CreateBookRequestDto.class)
        );
        verify(bookMapper, never()).toDto(any(Book.class));

        verifyNoMoreInteractions(
                bookRepository,
                bookMapper,
                pageMapper,
                pageMapperWithoutCategoryIds,
                bookSpecificationBuilder
        );
    }

    @Test
    @DisplayName("""
            Update book with existing id returns book dto
            """)
    void updateById_WithExistingId_ReturnsBookDto() {
        Book book = createBook();
        CreateBookRequestDto requestDto = createRequestBookDto();
        BookDto expectedBookDto = createBookDto(book);

        when(bookRepository.findById(ID_EXAMPLE)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        BookDto actualBookDto = bookService.updateById(ID_EXAMPLE, requestDto);

        assertEquals(expectedBookDto, actualBookDto);

        verify(bookRepository).findById(ID_EXAMPLE);
        verify(bookMapper).updateBookFromDto(book, requestDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);

        verifyNoMoreInteractions(
                bookRepository,
                bookMapper,
                pageMapper,
                pageMapperWithoutCategoryIds,
                bookSpecificationBuilder
        );
    }

    private BookSearchParameters createBookSearchParameters() {
        return new BookSearchParameters(
                new String[]{TITLE_PREFIX},
                new String[0],
                new String[0]
        );
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(ID_EXAMPLE);
        book.setTitle("The Hobbit");
        book.setAuthor("J.R.R. Tolkien");
        book.setIsbn("978000000001");
        book.setPrice(BigDecimal.valueOf(500));
        book.setDescription("Fantasy novel");
        book.setCoverImage("hobbit.jpg");
        book.setCategories(Set.of(new Category(ID_EXAMPLE)));

        return book;
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategories(Book book) {
        return new BookDtoWithoutCategoryIds()
                .setId(book.getId())
                .setAuthor(book.getAuthor())
                .setTitle(book.getTitle())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());
    }

    private BookDto createBookDto(Book book) {
        return new BookDto()
                .setId(book.getId())
                .setAuthor(book.getAuthor())
                .setTitle(book.getTitle())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(book.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet())
                );
    }

    private CreateBookRequestDto createRequestBookDto() {
        return new CreateBookRequestDto()
                .setTitle("Harry Potter and the Philosopher's Stone")
                .setAuthor("J.K. Rowling")
                .setIsbn("9780747532743")
                .setPrice(BigDecimal.valueOf(650))
                .setDescription("Fantasy novel about a young wizard")
                .setCoverImage("harry-potter.jpg")
                .setCategoryIds(Set.of(ID_EXAMPLE));
    }

    private PageDto<BookDtoWithoutCategoryIds> createPageDtoWithoutCategories(
            Page<BookDtoWithoutCategoryIds> bookPage
    ) {
        return new PageDto<>(
                bookPage.getContent(),
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages());
    }

    private PageDto<BookDto> createPageDto(
            Page<BookDto> bookPage
    ) {
        return new PageDto<>(
                bookPage.getContent(),
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages()
        );
    }
}
