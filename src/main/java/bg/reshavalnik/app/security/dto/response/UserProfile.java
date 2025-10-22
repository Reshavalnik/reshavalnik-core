package bg.reshavalnik.app.security.dto.response;

import bg.reshavalnik.app.security.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role roles;
}
