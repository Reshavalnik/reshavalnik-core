package bg.reshavalnik.app.exceptions.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST         ,
        reason = "Client mistake."
)
public class CreatePasswordException extends RuntimeException {

  public CreatePasswordException(String message) {
        super(message);
    }
}
