package bg.reshavalnik.app.security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken; // optional; cookie is primary
    private String tokenType;   // e.g., Bearer
    private UserProfile user;
}
