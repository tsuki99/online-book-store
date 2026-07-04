package mate.academy.springbootweb.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.model.ShoppingCart;
import mate.academy.springbootweb.model.User;
import mate.academy.springbootweb.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbootweb.service.user.UserProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShoppingCartProviderImpl implements ShoppingCartProvider {
    private static final String ENTITY_NOT_FOUND_MESSAGE = "Can't find shopping cart by user id: ";
    private final UserProvider userProvider;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public ShoppingCart getUserShoppingCart() {
        User user = userProvider.getCurrentUser();

        return shoppingCartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND_MESSAGE + user.getId()
                )
        );
    }
}
