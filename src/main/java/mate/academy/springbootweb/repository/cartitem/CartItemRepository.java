package mate.academy.springbootweb.repository.cartitem;

import java.util.Optional;
import mate.academy.springbootweb.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByShoppingCartIdAndBookId(Long cartId, Long bookId);
}
