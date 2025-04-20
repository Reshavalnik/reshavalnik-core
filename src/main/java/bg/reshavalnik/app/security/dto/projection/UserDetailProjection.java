package bg.reshavalnik.app.security.dto.projection;

import bg.reshavalnik.app.security.domain.Role;
import java.util.Set;

public interface UserDetailProjection {

    String getId();

    String getUsername();

    String getPassword();

    Set<Role> getRoles();
}
