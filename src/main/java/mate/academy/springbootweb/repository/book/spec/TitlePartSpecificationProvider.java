package mate.academy.springbootweb.repository.book.spec;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitlePartSpecificationProvider implements SpecificationProvider<Book> {
    private static final String FIELD_NAME = "title";

    @Override
    public String getKey() {
        return FIELD_NAME;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String param : params) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get(FIELD_NAME)),
                                "%" + param.toLowerCase() + "%"
                        )
                );
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
