package bg.reshavalnik.app.exceptions.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.UNAUTHORIZED,
        reason = "Wrong token"
)
public class TokenException  extends Exception{

    public TokenException(String message) {
        super(message);
    }
}
