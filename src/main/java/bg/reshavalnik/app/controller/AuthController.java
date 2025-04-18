package bg.reshavalnik.app.controller;

import bg.reshavalnik.app.exceptions.exeption.UserRegistrationException;
import bg.reshavalnik.app.security.dto.request.LoginRequest;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import bg.reshavalnik.app.service.security.SecurityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@Transactional
@AllArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final SecurityService securityService;

    @PostMapping("/signin")
    @Transactional(readOnly = true)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws UserRegistrationException {
        return ResponseEntity.ok(securityService.getAuthenticatUser(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws UserRegistrationException {
        return ResponseEntity.ok(securityService.getRegisterUser(signUpRequest));
    }

}