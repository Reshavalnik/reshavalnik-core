package bg.reshavalnik.app.controller.auth;

import bg.reshavalnik.app.repository.user.UserRepository;
import bg.reshavalnik.app.security.domain.User;
import bg.reshavalnik.app.security.dto.request.ChangePasswordRequest;
import bg.reshavalnik.app.security.dto.request.LoginRequest;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import bg.reshavalnik.app.security.dto.response.AuthResponse;
import bg.reshavalnik.app.security.dto.response.UserProfile;
import bg.reshavalnik.app.service.security.SecurityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final SecurityService securityService;
    private final UserRepository userRepository;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        var cookie = securityService.getAuthenticateUser(loginRequest);
        var profile = getCurrentUserProfile();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse.builder().tokenType("Bearer").user(profile).build());
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        var cookie = securityService.saveRegisterUser(signUpRequest);
        var profile = getCurrentUserProfile();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse.builder().tokenType("Bearer").user(profile).build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, securityService.changePassword(req).toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> me() {
        return ResponseEntity.ok(getCurrentUserProfile());
    }

    // Dev helper endpoint for quickly testing logged-in user
    @GetMapping("/dev/me")
    public ResponseEntity<UserProfile> devMe() {
        return me();
    }

    private UserProfile getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) return null;
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        return UserProfile.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .build();
    }
}
