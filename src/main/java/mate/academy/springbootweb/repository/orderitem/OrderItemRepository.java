package mate.academy.springbootweb.repository.orderitem;

import java.util.List;
import java.util.Optional;
import mate.academy.springbootweb.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderIdAndOrderUserId(Long orderId, Long userId);

    Optional<OrderItem> findByIdAndOrderIdAndOrderUserId(Long itemId, Long orderId, Long userId);
}
