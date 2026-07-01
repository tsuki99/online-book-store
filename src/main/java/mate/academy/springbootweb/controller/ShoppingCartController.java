package mate.academy.springbootweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.cartitem.AddCartItemRequestDto;
import mate.academy.springbootweb.dto.cartitem.CartItemDto;
import mate.academy.springbootweb.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootweb.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootweb.service.shoppingcart.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@Tag(name = "Shopping Cart", description = "Endpoints for managing user's shopping cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(
            summary = "Get user shopping cart",
            description = "Retrieve current authenticated user's shopping cart with all cart items"
    )
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ShoppingCartDto getUserShoppingCart() {
        return shoppingCartService.retrieveUserShoppingCart();
    }

    @Operation(
            summary = "Add book to shopping cart",
            description = "Add a book to user's shopping cart "
                    + "or increase quantity if already exists"
    )
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDto addBookToShoppingCart(
            @RequestBody @Valid AddCartItemRequestDto requestDto) {
        return shoppingCartService.addBooksToShoppingCart(requestDto);
    }

    @Operation(
            summary = "Update cart item quantity",
            description = "Update quantity of a specific book in the shopping cart by cart item id"
    )
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/items/{id}")
    public CartItemDto updateBookQuantityByBookId(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCartItemRequestDto requestDto
    ) {
        return shoppingCartService.updateBookQuantityByCartItemId(id, requestDto);
    }

    @Operation(
            summary = "Remove book from shopping cart",
            description = "Delete a cart item from user's shopping cart by cart item id"
    )
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookFromShoppingCart(@PathVariable Long id) {
        shoppingCartService.deleteBookFromShoppingCart(id);
    }
}
