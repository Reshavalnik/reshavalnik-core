package bg.reshavalnik.app.security.dto.request;

import bg.reshavalnik.app.anotation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


public abstract class BaseSignup {

    @NotBlank
    @Size(max = 60)
    @Email
    @JsonProperty(required = true)
    private String username;

    @NotBlank
    @ValidPassword
    @JsonProperty(required = true)
    private String password;

    @Size(max = 10)
    @Getter
    @Setter
    private String phone;

    public @NotBlank @Size(max = 50) @Email String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(max = 50) @Email String username) {
        this.username = username;
    }

    public @NotBlank @Size(min = 6, max = 40) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 6, max = 40) String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "BaseSignup{" +
                "username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
