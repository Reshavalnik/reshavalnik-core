package bg.reshavalnik.app.security.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import bg.reshavalnik.app.security.domain.Role;
import bg.reshavalnik.app.security.dto.projection.UserDetailProjection;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
public class UserDetailsTest {

    @Mock private UserDetailProjection projection;

    @Test
    void build_ShouldMapAllFieldsCorrectly() {
        when(projection.getId()).thenReturn("id123");
        when(projection.getUsername()).thenReturn("user1");
        when(projection.getPassword()).thenReturn("secret");
        Set<Role> roles = Set.of(Role.USER, Role.ADMIN);
        when(projection.getRoles()).thenReturn(roles);

        UserDetails userDetails = UserDetails.build(projection);

        assertEquals("id123", userDetails.getId());
        assertEquals("user1", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());

        Set<GrantedAuthority> expectedAuth =
                Set.of(
                        new SimpleGrantedAuthority(Role.USER.getAuthority()),
                        new SimpleGrantedAuthority(Role.ADMIN.getAuthority()));
        assertEquals(expectedAuth, Set.copyOf(userDetails.getAuthorities()));
    }

    @Test
    void toString_ShouldExcludePassword() {
        when(projection.getId()).thenReturn("idX");
        when(projection.getUsername()).thenReturn("john");
        when(projection.getPassword()).thenReturn("pass123");
        when(projection.getRoles()).thenReturn(Set.of());
        UserDetails details = UserDetails.build(projection);

        String str = details.toString();
        assertTrue(str.contains("idX"));
        assertTrue(str.contains("john"));
        assertFalse(str.contains("pass123"));
    }

    @Test
    void equalsAndHashCode_ShouldUseIdOnly() {
        when(projection.getId()).thenReturn("sameId");
        when(projection.getUsername()).thenReturn("u1");
        when(projection.getPassword()).thenReturn("pwd");
        when(projection.getRoles()).thenReturn(Set.of());

        var p2 = org.mockito.Mockito.mock(UserDetailProjection.class);
        when(p2.getId()).thenReturn("sameId");
        when(p2.getUsername()).thenReturn("u2");
        when(p2.getPassword()).thenReturn("pwd2");
        when(p2.getRoles()).thenReturn(Set.of());

        UserDetails d1 = UserDetails.build(projection);
        UserDetails d2 = UserDetails.build(p2);
        UserDetails d3 = UserDetails.build(p2);

        assertEquals(d1, d2);
        assertEquals(d2, d3);
        assertEquals(d1.hashCode(), d2.hashCode());

        when(p2.getId()).thenReturn("otherId");
        UserDetails dDifferent = UserDetails.build(p2);
        assertNotEquals(d1, dDifferent);
    }

    @Test
    void accountStatusFlags_ShouldBeTrue() {
        when(projection.getId()).thenReturn("i");
        when(projection.getUsername()).thenReturn("user");
        when(projection.getPassword()).thenReturn("pwd");
        when(projection.getRoles()).thenReturn(Set.of());
        UserDetails d = UserDetails.build(projection);

        assertTrue(d.isAccountNonExpired());
        assertTrue(d.isAccountNonLocked());
        assertTrue(d.isCredentialsNonExpired());
        assertTrue(d.isEnabled());
    }
}
