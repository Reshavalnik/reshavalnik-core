package bg.reshavalnik.app.security.security.jwt;

import bg.reshavalnik.app.security.security.services.UserDetails;
import io.jsonwebtoken.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    @Value("${jwt.secret.jwtExpirationMs}")
    private long jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setClaims(createClaims(userPrincipal))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    private Map<String, Object> createClaims(UserDetails userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userPrincipal.getUsername());
        claims.put("id", userPrincipal.getId());
        claims.put(
                "roles",
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
        return claims;
    }

    private SecretKeySpec getSigningKey() {
        return new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    public String getUserNameFromJwtToken(String token) {
        Claims claims =
                Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        return claims.getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
