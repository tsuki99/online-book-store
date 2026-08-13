package mate.academy.onlinebookstore.repository.role;

import java.util.Optional;
import mate.academy.onlinebookstore.model.Role;
import mate.academy.onlinebookstore.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
