package mate.academy.springbootweb.service.user;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.model.User;
import mate.academy.springbootweb.security.SecurityUtil;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {
    private final SecurityUtil securityUtil;

    @Override
    public User getCurrentUser() {
        return securityUtil.getCurrentUser();
    }
}
