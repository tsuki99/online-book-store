package mate.academy.springbootweb.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.springbootweb.config.MapperConfig;
import mate.academy.springbootweb.dto.book.BookDto;
import mate.academy.springbootweb.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbootweb.dto.book.CreateBookRequestDto;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    @Mapping(target = "categories", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    void updateBookFromDto(@MappingTarget Book book, CreateBookRequestDto dto);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        bookDto.setCategoryIds(categoryIds);
    }

    @AfterMapping
    default void setCategories(@MappingTarget Book book, CreateBookRequestDto requestDto) {
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(Category::new)
                .collect(Collectors.toSet());

        book.setCategories(categories);
    }
}
