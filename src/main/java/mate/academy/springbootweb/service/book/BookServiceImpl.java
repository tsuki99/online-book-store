package mate.academy.springbootweb.service.book;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.book.BookDto;
import mate.academy.springbootweb.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbootweb.dto.book.BookSearchParameters;
import mate.academy.springbootweb.dto.book.CreateBookRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.mapper.BookMapper;
import mate.academy.springbootweb.mapper.page.PageMapper;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.repository.SpecificationBuilder;
import mate.academy.springbootweb.repository.book.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private static final String NOT_FOUND_ENTITY_MESSAGE = "Can't find book by id: ";
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final PageMapper<BookDto> pageMapper;
    private final PageMapper<BookDtoWithoutCategoryIds> pageMapperWithoutCategoryIds;
    private final SpecificationBuilder<Book, BookSearchParameters> bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public PageDto<BookDto> findAll(Pageable pageable) {
        Page<BookDto> page = bookRepository.findAll(pageable).map(bookMapper::toDto);

        return pageMapper.toDto(page);
    }

    @Override
    public BookDto findBookById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(NOT_FOUND_ENTITY_MESSAGE + id)));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(NOT_FOUND_ENTITY_MESSAGE + id));

        bookMapper.updateBookFromDto(book, requestDto);

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public PageDto<BookDto> search(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder
                .build(searchParameters);

        Page<BookDto> page = bookRepository
                .findAll(bookSpecification, pageable)
                .map(bookMapper::toDto);

        return pageMapper.toDto(page);
    }

    @Override
    public PageDto<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id, Pageable pageable) {
        return pageMapperWithoutCategoryIds.toDto(bookRepository.findAllByCategoryId(id, pageable)
                .map(bookMapper::toDtoWithoutCategories));

    }
}
