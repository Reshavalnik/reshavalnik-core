package bg.reshavalnik.app.security.domain;

import bg.reshavalnik.app.anotation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user")
public class User {

    @Id private String id;

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank @ValidPassword private String password;

    private String firstName;

    private String lastName;

    @Size(max = 20)
    private String nickname;

    @Email private String email;

    private String phone;

    private Set<Role> roles;
}
