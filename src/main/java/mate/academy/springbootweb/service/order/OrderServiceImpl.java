package mate.academy.springbootweb.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.order.CreateOrderRequestDto;
import mate.academy.springbootweb.dto.order.OrderDto;
import mate.academy.springbootweb.dto.order.UpdateOrderRequestDto;
import mate.academy.springbootweb.dto.orderitem.OrderItemDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.exception.OrderProcessingException;
import mate.academy.springbootweb.mapper.OrderItemMapper;
import mate.academy.springbootweb.mapper.OrderMapper;
import mate.academy.springbootweb.mapper.page.PageMapper;
import mate.academy.springbootweb.model.CartItem;
import mate.academy.springbootweb.model.Order;
import mate.academy.springbootweb.model.OrderItem;
import mate.academy.springbootweb.model.ShoppingCart;
import mate.academy.springbootweb.model.User;
import mate.academy.springbootweb.model.enums.Status;
import mate.academy.springbootweb.repository.order.OrderRepository;
import mate.academy.springbootweb.repository.orderitem.OrderItemRepository;
import mate.academy.springbootweb.service.shoppingcart.ShoppingCartProvider;
import mate.academy.springbootweb.service.shoppingcart.ShoppingCartService;
import mate.academy.springbootweb.service.user.UserProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final String NOT_FOUND_ORDER_MESSAGE = "Can't find order by id: ";
    private static final String NOT_FOUND_ITEM_MESSAGE = "Can't find order item by id: ";
    private static final String EMPTY_SHOPPING_CART_MESSAGE =
            "Can't place an order, shopping cart is empty!";
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartProvider shoppingCartProvider;
    private final UserProvider userProvider;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final PageMapper<OrderDto> pageMapper;

    @Override
    public OrderDto placeOrder(CreateOrderRequestDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCart();

        if (shoppingCart.getCartItems().isEmpty()) {
            throw new OrderProcessingException(EMPTY_SHOPPING_CART_MESSAGE);
        }

        Order newOrder = createNewOrder(shoppingCart, requestDto.getShippingAddress());
        Order savedOrder = orderRepository.save(newOrder);

        shoppingCartService.clear();

        return orderMapper.toDto(savedOrder);
    }

    @Override
    public PageDto<OrderDto> findOrdersHistory(Pageable pageable) {
        User user = getUser();

        Page<OrderDto> page = orderRepository.findByUserId(user.getId(), pageable)
                .map(orderMapper::toDto);

        return pageMapper.toDto(page);
    }

    @Override
    public List<OrderItemDto> findAllItemsByOrderId(Long orderId) {
        User user = getUser();

        return orderItemRepository.findByOrderIdAndOrderUserId(orderId, user.getId()).stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto findItemByOrderAndItemIds(Long orderId, Long itemId) {
        User user = getUser();

        return orderItemRepository.findByIdAndOrderIdAndOrderUserId(itemId, orderId, user.getId())
                .map(orderItemMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException(NOT_FOUND_ITEM_MESSAGE + itemId)
        );
    }

    @Override
    public OrderDto updateOrderStatus(Long id, UpdateOrderRequestDto requestDto) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(NOT_FOUND_ORDER_MESSAGE + id)
        );

        Order updatedOrder = orderMapper.updateOrder(order, requestDto);

        return orderMapper.toDto(updatedOrder);
    }

    private Order createNewOrder(ShoppingCart shoppingCart, String shippingAddress) {
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setStatus(Status.PENDING);
        order.setTotal(calculateTotal(shoppingCart.getCartItems()));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setOrderItems(createOrderItems(shoppingCart.getCartItems(), order));

        return order;
    }

    private static BigDecimal calculateTotal(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item
                        .getBook()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Set<OrderItem> createOrderItems(Set<CartItem> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> toOrderItem(cartItem, order))
                .collect(Collectors.toSet());
    }

    private static OrderItem toOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice());

        return orderItem;
    }

    private ShoppingCart getShoppingCart() {
        return shoppingCartProvider.getUserShoppingCart();
    }

    private User getUser() {
        return userProvider.getCurrentUser();
    }
}
