package mate.academy.springbootweb.mapper;

import mate.academy.springbootweb.config.MapperConfig;
import mate.academy.springbootweb.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootweb.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
