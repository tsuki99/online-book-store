package mate.academy.onlinebookstore.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.onlinebookstore.service.user.UserProvider;
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
