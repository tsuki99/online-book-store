package mate.academy.onlinebookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    private static final String EXPECTED_TITLE = "Spring in Action";
    private static final Long ID_EXAMPLE = 2L;
    private static final int DEFAULT_PAGE_SIZE = 6;
    private static final int DEFAULT_FIRST_ELEMENT_NUMBER = 0;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Find books by existing category id
            """)
    @Sql(scripts = "classpath:database/categories/add-two-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/add-three-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books_categories/add-books-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books_categories/remove-books-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/remove-all-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidId_ReturnsPage() {
        Page<Book> actual = bookRepository.findAllByCategoryId(
                ID_EXAMPLE,
                PageRequest.of(DEFAULT_FIRST_ELEMENT_NUMBER, DEFAULT_PAGE_SIZE)
        );

        assertEquals(ID_EXAMPLE, actual.getTotalElements());
        assertTrue(actual.getContent().stream()
                .anyMatch(book -> book.getTitle().equals(EXPECTED_TITLE))
        );
    }
}
