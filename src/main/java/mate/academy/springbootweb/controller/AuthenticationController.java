package mate.academy.springbootweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootweb.dto.user.UserResponseDto;
import mate.academy.springbootweb.exception.RegistrationException;
import mate.academy.springbootweb.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {
    private final UserService userService;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and saves their information in the database"
    )
    @PostMapping("/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {

        return userService.register(requestDto);
    }
}
