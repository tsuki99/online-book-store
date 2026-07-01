package mate.academy.springbootweb.service.book;

import mate.academy.springbootweb.dto.book.BookDto;
import mate.academy.springbootweb.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbootweb.dto.book.BookSearchParameters;
import mate.academy.springbootweb.dto.book.CreateBookRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    PageDto<BookDto> findAll(Pageable pageable);

    BookDto findBookById(Long id);

    void deleteById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    PageDto<BookDto> search(BookSearchParameters searchParameters, Pageable pageable);

    PageDto<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id, Pageable pageable);
}
