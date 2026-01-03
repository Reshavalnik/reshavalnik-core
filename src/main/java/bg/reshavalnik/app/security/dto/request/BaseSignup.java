package bg.reshavalnik.app.security.dto.request;

import bg.reshavalnik.app.anotation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString(exclude = "password")
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseSignup {

    @NotBlank
    @Size(max = 60)
    String username;

    @NotBlank
    @Email
    @Size(max = 100)
    String email;

    @Size(
            min = 6,
            max = 40,
            message = "Password cannot be less than 6 characters amd more then 40 characters")
    @NotBlank
    @ValidPassword
    String password;

    @Size(max = 10)
    String phone;
}
