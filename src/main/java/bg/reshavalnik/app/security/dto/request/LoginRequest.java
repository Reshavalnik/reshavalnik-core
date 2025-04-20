package bg.reshavalnik.app.security.dto.request;

import bg.reshavalnik.app.anotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank private String username;

    @NotBlank @ValidPassword private String password;
}
