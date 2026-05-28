package mate.academy.springbootweb.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.model.Book;
import mate.academy.springbootweb.repository.SpecificationProvider;
import mate.academy.springbootweb.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Can't find any providers with key: " + key));
    }
}
