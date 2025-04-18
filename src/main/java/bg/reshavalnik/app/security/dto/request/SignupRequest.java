package bg.reshavalnik.app.security.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest extends BaseSignup {

    @Size(max = 20)
    private String nickname;

    private String firstName;

    private String lastName;

    @Override
    public String toString() {
        return "SignupRequest{" +
                "nickname='" + nickname + '\'' +
                ", role=" + firstName +
                ", names='" + lastName + '\'' +
                '}';
    }
}
