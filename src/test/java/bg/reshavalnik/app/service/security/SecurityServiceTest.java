package bg.reshavalnik.app.service.security;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.USERNAME_IS_ALREADY_TAKEN;
import static bg.reshavalnik.app.exceptions.message.ErrorMessage.WRONG_EMAIL_OR_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import bg.reshavalnik.app.mapper.UserMapper;
import bg.reshavalnik.app.repository.UserRepository;
import bg.reshavalnik.app.security.dto.request.LoginRequest;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import bg.reshavalnik.app.security.dto.response.JwtResponse;
import bg.reshavalnik.app.security.security.jwt.JwtUtils;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder encoder;
    @Mock private UserMapper userMapper;

    @InjectMocks private SecurityService securityService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAuthenticateUser_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword("pass1");
        Authentication auth =
                new UsernamePasswordAuthenticationToken("user1", "pass1", Collections.emptyList());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateJwtToken(auth)).thenReturn("token123");

        JwtResponse response = securityService.getAuthenticateUser(request);

        assertEquals("token123", response.getToken());
        assertSame(auth, SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils).generateJwtToken(auth);
    }

    @Test
    void getAuthenticateUser_InvalidCredentials_Throws() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword("pass1");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad cred"));

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> securityService.getAuthenticateUser(request));
        assertEquals(WRONG_EMAIL_OR_PASSWORD, ex.getMessage());
    }

    @Test
    void saveRegisterUser_Success() {
        SignupRequest signup = new SignupRequest();
        signup.setUsername("newuser");
        signup.setPassword("pass123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(encoder.encode("pass123")).thenReturn("encodedPass");
        when(userMapper.mapToUser(any()))
                .thenAnswer(
                        invocation -> {
                            SignupRequest req = invocation.getArgument(0);
                            var u = new bg.reshavalnik.app.security.domain.User();
                            u.setPassword(req.getPassword());
                            return u;
                        });
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "newuser", "pass123", Collections.emptyList());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateJwtToken(auth)).thenReturn("jwt123");

        JwtResponse response = securityService.saveRegisterUser(signup);

        assertEquals("jwt123", response.getToken());
        ArgumentCaptor<bg.reshavalnik.app.security.domain.User> userCaptor =
                ArgumentCaptor.forClass(bg.reshavalnik.app.security.domain.User.class);
        verify(userRepository).save(userCaptor.capture());
        var saved = userCaptor.getValue();
        assertEquals("encodedPass", saved.getPassword());
        assertNotNull(saved.getRoles());
        assertTrue(saved.getRoles().equals(bg.reshavalnik.app.security.domain.Role.USER));
    }

    @Test
    void saveRegisterUser_UsernameExists_Throws() {
        SignupRequest signup = new SignupRequest();
        signup.setUsername("existing");
        signup.setPassword("pass");
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> securityService.saveRegisterUser(signup));
        assertEquals(USERNAME_IS_ALREADY_TAKEN, ex.getMessage());
    }

    @Test
    void saveRegisterUser_AdminUsername_Throws() {
        SignupRequest signup = new SignupRequest();
        signup.setUsername("adminUser");
        signup.setPassword("pass");

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> securityService.saveRegisterUser(signup));
        assertEquals(USERNAME_IS_ALREADY_TAKEN, ex.getMessage());
    }
}
