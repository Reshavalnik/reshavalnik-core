package bg.reshavalnik.app.exceptions.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "Role is not found."
)
public class RoleException extends RuntimeException {
    public RoleException(String message) {
        super(message);
    }
}
