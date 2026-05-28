package mate.academy.springbootweb.mapper;

import mate.academy.springbootweb.config.MapperConfig;
import mate.academy.springbootweb.dto.BookDto;
import mate.academy.springbootweb.dto.CreateBookRequestDto;
import mate.academy.springbootweb.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
