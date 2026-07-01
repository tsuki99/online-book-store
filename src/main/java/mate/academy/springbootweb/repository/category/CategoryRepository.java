package mate.academy.springbootweb.repository.category;

import mate.academy.springbootweb.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
