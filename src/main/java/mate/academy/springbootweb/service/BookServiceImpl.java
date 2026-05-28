package mate.academy.springbootweb.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.BookDto;
import mate.academy.springbootweb.dto.CreateBookRequestDto;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.mapper.BookMapper;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.repository.BookRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findBookById(Long id) {
        return bookMapper.toDto(bookRepository.findBookById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find book by id: " + id)));
    }
}
