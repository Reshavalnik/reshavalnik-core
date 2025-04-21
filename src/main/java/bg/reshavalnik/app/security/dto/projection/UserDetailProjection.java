package bg.reshavalnik.app.security.dto.projection;

import bg.reshavalnik.app.security.domain.Role;

public interface UserDetailProjection {

    String getId();

    String getUsername();

    String getPassword();

    Role getRoles();
}
