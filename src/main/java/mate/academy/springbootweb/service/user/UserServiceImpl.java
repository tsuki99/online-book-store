package mate.academy.springbootweb.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootweb.dto.user.UserResponseDto;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.exception.RegistrationException;
import mate.academy.springbootweb.mapper.UserMapper;
import mate.academy.springbootweb.model.User;
import mate.academy.springbootweb.model.enums.RoleName;
import mate.academy.springbootweb.repository.role.RoleRepository;
import mate.academy.springbootweb.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {

        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("User with email: " + email + " already exists");
        }

        User user = userMapper.toModel(requestDto);
        user.setRoles(Set.of(roleRepository.findByName(RoleName.USER).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find role by name: " + RoleName.USER.name())))
        );
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User savedUser = userRepository.save(user);

        return userMapper.toUserResponseDto(savedUser);
    }
}
