package mate.academy.onlinebookstore.service.order;

import java.util.List;
import mate.academy.onlinebookstore.dto.order.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import mate.academy.onlinebookstore.dto.page.PageDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeOrder(CreateOrderRequestDto requestDto);

    PageDto<OrderDto> findOrdersHistory(Pageable pageable);

    List<OrderItemDto> findAllItemsByOrderId(Long orderId);

    OrderItemDto findItemByOrderAndItemIds(Long orderId, Long itemId);

    OrderDto updateOrderStatus(Long id, UpdateOrderRequestDto requestDto);
}
