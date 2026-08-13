package mate.academy.onlinebookstore.service.user;

import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.security.SecurityUtil;
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
