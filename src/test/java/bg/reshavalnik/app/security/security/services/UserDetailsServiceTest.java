package bg.reshavalnik.app.security.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import bg.reshavalnik.app.repository.user.UserRepository;
import bg.reshavalnik.app.security.domain.Role;
import bg.reshavalnik.app.security.dto.projection.UserDetailProjection;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @Mock private UserRepository userRepository;

    @Mock private UserDetailProjection projection;

    @InjectMocks private UserDetailsService service;

    @Test
    void loadUserByUsername_Success() {
        String username = "user1";
        when(userRepository.getUserByUsername(username)).thenReturn(Optional.of(projection));
        when(projection.getId()).thenReturn("id123");
        when(projection.getUsername()).thenReturn(username);
        when(projection.getPassword()).thenReturn("secret");
        Role roles = Role.USER;
        when(projection.getRoles()).thenReturn(roles);

        var details = service.loadUserByUsername(username);

        assertNotNull(details);
        assertEquals(username, details.getUsername());
        assertEquals(1, details.getAuthorities().size());
        assertTrue(
                details.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(Role.USER.name())));

        assertTrue(details instanceof UserDetails);
        UserDetails ud = (UserDetails) details;
        assertEquals("id123", ud.getId());
    }

    @Test
    void loadUserByUsername_NotFound_Throws() {
        String username = "unknown";
        when(userRepository.getUserByUsername(username)).thenReturn(Optional.empty());

        var ex =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> service.loadUserByUsername(username));
        assertEquals("User Not Found with username: " + username, ex.getMessage());
    }
}
