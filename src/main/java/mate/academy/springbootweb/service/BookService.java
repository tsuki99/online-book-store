package mate.academy.springbootweb.service;

import java.util.List;
import mate.academy.springbootweb.dto.BookDto;
import mate.academy.springbootweb.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findBookById(Long id);
}
