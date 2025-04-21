package bg.reshavalnik.app.service.security;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.*;

import bg.reshavalnik.app.mapper.UserMapper;
import bg.reshavalnik.app.repository.UserRepository;
import bg.reshavalnik.app.security.domain.Role;
import bg.reshavalnik.app.security.domain.User;
import bg.reshavalnik.app.security.dto.request.LoginRequest;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import bg.reshavalnik.app.security.dto.response.JwtResponse;
import bg.reshavalnik.app.security.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Data
@AllArgsConstructor
@Service
public class SecurityService {
    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public JwtResponse getAuthenticateUser(@Valid LoginRequest loginRequest) {
        if (loginRequest.getUsername().contains("admin")) {
            throw new IllegalArgumentException(CANNOT_ENTER_AS_ADMIN + loginRequest.getUsername());
        }

        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException ex) {
            log.error("Authentication failed: {}", ex.getMessage());
            throw new IllegalArgumentException(WRONG_EMAIL_OR_PASSWORD);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);
        log.info("User {} authenticated successfully", loginRequest.getUsername());
        return new JwtResponse(token);
    }

    @Transactional
    public JwtResponse saveRegisterUser(@Valid SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())
                || signUpRequest.getUsername().contains("admin")) {
            throw new IllegalArgumentException(USERNAME_IS_ALREADY_TAKEN);
        }

        String rawPassword = signUpRequest.getPassword();
        signUpRequest.setPassword(encoder.encode(rawPassword));
        User user = userMapper.mapToUser(signUpRequest);
        user.setRoles(Role.USER);
        userRepository.save(user);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                signUpRequest.getUsername(), rawPassword));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return new JwtResponse(jwt);
    }
}
