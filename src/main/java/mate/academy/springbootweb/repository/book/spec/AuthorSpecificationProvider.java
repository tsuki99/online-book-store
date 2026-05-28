package mate.academy.springbootweb.repository.book.spec;

import java.util.Arrays;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String FIELD_NAME = "author";

    @Override
    public String getKey() {
        return FIELD_NAME;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(FIELD_NAME).in(Arrays.stream(params).toArray());
    }
}
