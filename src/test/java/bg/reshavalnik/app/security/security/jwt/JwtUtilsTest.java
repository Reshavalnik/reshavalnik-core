package bg.reshavalnik.app.security.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import bg.reshavalnik.app.security.security.services.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    // 32-byte key encoded in Base64 ("AAAAAAAA...AAA=")
    private static final String SECRET = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
    private static final long EXPIRATION_MS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", EXPIRATION_MS);
    }

    @Test
    void generateJwtToken_and_validate_and_getUserName() {
        UserDetails userPrincipal =
                UserDetails.builder()
                        .id("id123")
                        .username("user1")
                        .password("secret")
                        .authorities(List.of(new SimpleGrantedAuthority("USER")))
                        .build();
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities());

        String token = jwtUtils.generateJwtToken(auth);

        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("user1", jwtUtils.getUserNameFromJwtToken(token));

        Claims claims =
                Jwts.parserBuilder()
                        .setSigningKey(getSigningKey(SECRET))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        assertEquals("id123", claims.get("id", String.class));
        assertEquals(List.of("USER"), claims.get("roles", List.class));
    }

    @Test
    void validateJwtToken_InvalidToken_ReturnsFalse() {
        String badToken = "this.is.not.a.valid.token";
        assertFalse(jwtUtils.validateJwtToken(badToken));
    }

    private SecretKeySpec getSigningKey(String secret) {
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }
}
