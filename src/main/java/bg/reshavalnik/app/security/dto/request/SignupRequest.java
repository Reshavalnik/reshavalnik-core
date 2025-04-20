package bg.reshavalnik.app.security.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest extends BaseSignup {

    @Size(max = 20)
    String nickname;

    String firstName;

    String lastName;
}
