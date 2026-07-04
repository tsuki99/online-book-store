package mate.academy.springbootweb.mapper;

import mate.academy.springbootweb.config.MapperConfig;
import mate.academy.springbootweb.dto.order.OrderDto;
import mate.academy.springbootweb.dto.order.UpdateOrderRequestDto;
import mate.academy.springbootweb.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    Order updateOrder(@MappingTarget Order order, UpdateOrderRequestDto requestDto);
}
