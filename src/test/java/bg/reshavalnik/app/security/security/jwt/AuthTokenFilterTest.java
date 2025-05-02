package bg.reshavalnik.app.security.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import bg.reshavalnik.app.security.security.services.UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @InjectMocks private AuthTokenFilter authTokenFilter;

    @Mock private JwtUtils jwtUtils;

    @Mock private UserDetailsService userDetailsService;

    @Mock private HttpServletRequest request;

    @Mock private HttpServletResponse response;

    @Mock private FilterChain filterChain;

    private static final String COOKIE_NAME = "JWT";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_withValidCookie_setsAuthentication()
            throws ServletException, IOException {
        String token = "validToken";
        Cookie jwtCookie = new Cookie(COOKIE_NAME, token);
        when(request.getCookies()).thenReturn(new Cookie[] {jwtCookie});

        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("user1");
        UserDetails userDetails =
                User.withUsername("user1")
                        .password("irrelevant")
                        .authorities(new SimpleGrantedAuthority("USER"))
                        .build();
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication трябва да е сетнато");
        assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        assertEquals("user1", auth.getName());
        assertEquals(1, auth.getAuthorities().size());
        assertEquals("USER", auth.getAuthorities().iterator().next().getAuthority());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withInvalidCookie_doesNotSetAuthentication()
            throws ServletException, IOException {

        String token = "badToken";
        Cookie jwtCookie = new Cookie(COOKIE_NAME, token);
        when(request.getCookies()).thenReturn(new Cookie[] {jwtCookie});
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication(),
                "Authentication не трябва да се сетва");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withNoCookie_doesNotSetAuthentication()
            throws ServletException, IOException {
        when(request.getCookies()).thenReturn(null);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication(),
                "Authentication не трябва да се сетва");
        verify(filterChain).doFilter(request, response);
    }
}
