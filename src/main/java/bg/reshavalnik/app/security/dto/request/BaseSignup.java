package bg.reshavalnik.app.security.dto.request;

import bg.reshavalnik.app.anotation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(required = true)
    String username;

    @NotBlank
    @Email
    @Size(max = 100)
    @JsonProperty(required = true)
    String email;

    @NotBlank
    @ValidPassword
    @JsonProperty(required = true)
    String password;

    @Size(max = 10)
    String phone;
}
