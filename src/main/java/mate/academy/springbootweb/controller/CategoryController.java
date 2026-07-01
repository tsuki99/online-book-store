package mate.academy.springbootweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbootweb.dto.category.CategoryDto;
import mate.academy.springbootweb.dto.category.CreateCategoryRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.service.book.BookService;
import mate.academy.springbootweb.service.category.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Endpoints for managing categories and related books")
public class CategoryController {
    private static final String FIELD_TITLE_CATEGORY = "name";
    private static final String FIELD_TITLE_BOOK = "title";
    private static final String DEFAULT_SORT_DESCRIPTION =
            "with default sorting by name in ascending order.";
    private final CategoryService categoryService;
    private final BookService bookService;

    @Operation(
            summary = "Create category",
            description = "Create a new category in the database"
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @Operation(
            summary = "Get all categories",
            description = "Retrieve all categories " + DEFAULT_SORT_DESCRIPTION
    )
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public PageDto<CategoryDto> getAll(
            @PageableDefault(
                    sort = FIELD_TITLE_CATEGORY,
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @Operation(
            summary = "Get category by id",
            description = "Retrieve a category by its id"
    )
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @Operation(
            summary = "Update category",
            description = "Update an existing category by its id"
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public CategoryDto updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CreateCategoryRequestDto requestDto
    ) {
        return categoryService.update(id, requestDto);
    }

    @Operation(
            summary = "Delete category",
            description = "Delete a category by its id"
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @Operation(
            summary = "Get books by category",
            description = "Retrieve books that belong to a specific category "
                    + DEFAULT_SORT_DESCRIPTION
    )
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}/books")
    public PageDto<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @PathVariable Long id,
            @PageableDefault(
                    sort = FIELD_TITLE_BOOK,
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return bookService.findBooksByCategoryId(id, pageable);
    }
}
