package bg.reshavalnik.app.exceptions.exeption;

import java.io.Serial;

public class UserRegistrationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserRegistrationException(String message) {
        super(message);
    }
}