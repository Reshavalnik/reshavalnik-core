package bg.reshavalnik.app.security.dto.request;

import bg.reshavalnik.app.anotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank private String username;

    @NotBlank private String currentPassword;

    @NotBlank @ValidPassword private String newPassword;
}
