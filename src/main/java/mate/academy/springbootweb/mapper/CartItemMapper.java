package mate.academy.springbootweb.mapper;

import mate.academy.springbootweb.config.MapperConfig;
import mate.academy.springbootweb.dto.cartitem.CartItemDto;
import mate.academy.springbootweb.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootweb.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    void updateCartItemFromDto(
            @MappingTarget CartItem cartItem,
            UpdateCartItemRequestDto requestDto
    );
}
