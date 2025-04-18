package bg.reshavalnik.app.service.role;

import bg.reshavalnik.app.repository.RoleRepository;
import bg.reshavalnik.app.security.domain.ERole;
import bg.reshavalnik.app.security.domain.Role;
import bg.reshavalnik.app.security.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Transactional
@AllArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public void assignRoleToUser(User user, ERole roleType) {
        Role userRole = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("Error: Role " + roleType + " is not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
    }

}
