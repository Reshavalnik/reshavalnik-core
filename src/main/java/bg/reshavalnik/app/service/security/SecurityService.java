package bg.reshavalnik.app.service.security;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.*;

import bg.reshavalnik.app.mapper.UserMapper;
import bg.reshavalnik.app.repository.UserRepository;
import bg.reshavalnik.app.security.domain.Role;
import bg.reshavalnik.app.security.domain.User;
import bg.reshavalnik.app.security.dto.request.ChangePasswordRequest;
import bg.reshavalnik.app.security.dto.request.LoginRequest;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import bg.reshavalnik.app.security.security.jwt.JwtUtils;
import bg.reshavalnik.app.security.security.services.UserDetails;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResponseCookie getAuthenticateUser(@Valid LoginRequest loginRequest) {
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

        return buildJwt(token);
    }

    @Transactional
    public ResponseCookie saveRegisterUser(@Valid SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
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
        String token = jwtUtils.generateJwtToken(authentication);
        log.info("User {} registered successfully", signUpRequest.getUsername());

        return buildJwt(token);
    }

    private ResponseCookie buildJwt(String token) {
        return ResponseCookie.from("JWT", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtUtils.getJwtExpirationMs() / 1000)
                .sameSite("Strict")
                .build();
    }

    @Transactional
    public ResponseCookie changePassword(ChangePasswordRequest req) {
        if (!userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException(WRONG_EMAIL_OR_PASSWORD);
        }

        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    req.getUsername(), req.getCurrentPassword()));
        } catch (Exception e) {
            log.error(WRONG_OLD_PASSWORD + e.getMessage());
            throw new IllegalArgumentException(WRONG_EMAIL_OR_PASSWORD);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user =
                userRepository
                        .findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);
        log.info("User {} changed password successfully", user.getUsername());

        return buildJwt(token);
    }
}
