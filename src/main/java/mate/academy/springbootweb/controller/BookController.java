package mate.academy.springbootweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.book.BookDto;
import mate.academy.springbootweb.dto.book.BookSearchParameters;
import mate.academy.springbootweb.dto.book.CreateBookRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.service.book.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
@Tag(name = "Books", description = "Endpoints for managing books")
public class BookController {
    private static final String FIELD_TITLE = "title";
    private static final String DEFAULT_SORT_DESCRIPTION =
            "with default sorting by title in ascending order.";
    private final BookService bookService;

    @Operation(
            summary = "Get all books",
            description = "Retrieve all books from db " + DEFAULT_SORT_DESCRIPTION
    )
    @GetMapping
    public PageDto<BookDto> getAll(@PageableDefault(sort = FIELD_TITLE,
            direction = Sort.Direction.ASC) Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(
            summary = "Get book by id",
            description = "Retrieve a book by its id"
    )
    @GetMapping("/{id}")
    public BookDto getById(@PathVariable Long id) {
        return bookService.findBookById(id);
    }

    @Operation(
            summary = "Save book",
            description = "Save a new book to the database"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto save(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @Operation(
            summary = "Delete book",
            description = "Delete a book by its id"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @Operation(
            summary = "Update book",
            description = "Update an existing book by its id"
    )
    @PutMapping("/{id}")
    public BookDto updateById(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto requestDto
    ) {
        return bookService.updateById(id, requestDto);
    }

    @Operation(
            summary = "Search books",
            description = "Search books by parameters " + DEFAULT_SORT_DESCRIPTION
    )
    @GetMapping("/search")
    public PageDto<BookDto> search(BookSearchParameters bookSearchParameters,
                                   @PageableDefault(sort = FIELD_TITLE,
                                           direction = Sort.Direction.ASC) Pageable pageable) {
        return bookService.search(bookSearchParameters, pageable);
    }
}
