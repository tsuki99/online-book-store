package mate.academy.springbootweb.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.BookSearchParameters;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.repository.SpecificationBuilder;
import mate.academy.springbootweb.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder
        implements SpecificationBuilder<Book, BookSearchParameters> {

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (searchParameters.titleParts() != null && searchParameters.titleParts().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(searchParameters.titleParts()));
        }

        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(searchParameters.authors()));
        }

        if (searchParameters.isbns() != null && searchParameters.isbns().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.isbns()));
        }

        return spec;
    }
}
