package mate.academy.springbootweb.service.user;

import mate.academy.springbootweb.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootweb.dto.user.UserResponseDto;
import mate.academy.springbootweb.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
