package bg.reshavalnik.app.validators;

import bg.reshavalnik.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserValidator {

    private final UserRepository userRepository;
}
