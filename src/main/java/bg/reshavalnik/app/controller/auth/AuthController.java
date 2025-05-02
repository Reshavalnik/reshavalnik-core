package bg.reshavalnik.app.controller.auth;

import bg.reshavalnik.app.security.dto.request.ChangePasswordRequest;
import bg.reshavalnik.app.security.dto.request.LoginRequest;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import bg.reshavalnik.app.service.security.SecurityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final SecurityService securityService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        securityService.getAuthenticateUser(loginRequest).toString())
                .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        securityService.saveRegisterUser(signUpRequest).toString())
                .build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, securityService.changePassword(req).toString())
                .build();
    }
}
