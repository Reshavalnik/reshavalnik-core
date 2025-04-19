package bg.reshavalnik.app.exceptions.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "Client mistake."
)
public class UserException extends Exception {

    public UserException(String message) {
        super(message);
    }
}
