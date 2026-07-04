package mate.academy.springbootweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.order.CreateOrderRequestDto;
import mate.academy.springbootweb.dto.order.OrderDto;
import mate.academy.springbootweb.dto.order.UpdateOrderRequestDto;
import mate.academy.springbootweb.dto.orderitem.OrderItemDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.service.order.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Endpoints for managing orders")
public class OrderController {
    private static final String DEFAULT_SORT_FIELD = "orderDate";
    private final OrderService orderService;

    @Operation(
            summary = "Place an order",
            description = "Create a new order from the current user's shopping cart"
    )
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeOrder(@RequestBody @Valid CreateOrderRequestDto requestDto) {
        return orderService.placeOrder(requestDto);
    }

    @Operation(
            summary = "Get order history",
            description = "Retrieve the current user's order history "
                    + "with default sorting by order date in descending order"
    )
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public PageDto<OrderDto> getOrdersHistory(
            @PageableDefault(
                    sort = DEFAULT_SORT_FIELD,
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return orderService.findOrdersHistory(pageable);
    }

    @Operation(
            summary = "Get all order items",
            description = "Retrieve all items belonging to a specific order"
    )
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getAllItemsByOrderId(@PathVariable Long orderId) {
        return orderService.findAllItemsByOrderId(orderId);
    }

    @Operation(
            summary = "Get order item",
            description = "Retrieve a specific item from a specific order"
    )
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items/{id}")
    public OrderItemDto getItemByOrderAndItemIds(
            @PathVariable Long orderId,
            @PathVariable("id") Long itemId
    ) {
        return orderService.findItemByOrderAndItemIds(orderId, itemId);
    }

    @Operation(
            summary = "Update order status",
            description = "Update the status of an existing order"
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}")
    public OrderDto updateOrderStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderRequestDto requestDto
    ) {
        return orderService.updateOrderStatus(id, requestDto);
    }
}
