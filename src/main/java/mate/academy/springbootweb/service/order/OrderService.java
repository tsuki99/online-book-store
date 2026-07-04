package mate.academy.springbootweb.service.order;

import java.util.List;
import mate.academy.springbootweb.dto.order.CreateOrderRequestDto;
import mate.academy.springbootweb.dto.order.OrderDto;
import mate.academy.springbootweb.dto.order.UpdateOrderRequestDto;
import mate.academy.springbootweb.dto.orderitem.OrderItemDto;
import mate.academy.springbootweb.dto.page.PageDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeOrder(CreateOrderRequestDto requestDto);

    PageDto<OrderDto> findOrdersHistory(Pageable pageable);

    List<OrderItemDto> findAllItemsByOrderId(Long orderId);

    OrderItemDto findItemByOrderAndItemIds(Long orderId, Long itemId);

    OrderDto updateOrderStatus(Long id, UpdateOrderRequestDto requestDto);
}
