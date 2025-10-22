package bg.reshavalnik.app.service.security;

import bg.reshavalnik.app.repository.user.UserRepository;
import bg.reshavalnik.app.security.domain.Role;
import bg.reshavalnik.app.security.domain.User;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * Service responsible for Social Authentication (Google/Facebook).
 *
 * <p>Validates third-party tokens server-side, finds or creates the local user by email,
 * and issues a JWT which is returned as an HttpOnly cookie and a raw token string.</p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class SocialAuthService {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    private final RestClient restClient = RestClient.create();

    @Data
    @Builder
    public static class SocialLoginResult {
        private String token;
        private ResponseCookie cookie;
        private User user;
    }

    @Transactional
    public SocialLoginResult loginWithGoogleIdToken(String idToken) {
        if (!StringUtils.hasText(idToken)) {
            throw new IllegalArgumentException("Missing Google id_token");
        }
        try {
            Map<String, Object> resp = restClient
                    .get()
                    .uri(URI.create("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken))
                    .retrieve()
                    .body(Map.class);
            if (resp == null || resp.get("email") == null) {
                log.warn("Google token validation failed: response={}, id_hint={}", resp, maskToken(idToken));
                throw new IllegalArgumentException("Invalid or expired Google token");
            }
            String email = String.valueOf(resp.get("email"));
            String givenName = String.valueOf(resp.getOrDefault("given_name", ""));
            String familyName = String.valueOf(resp.getOrDefault("family_name", ""));
            SocialLoginResult result = linkUserAndIssueToken(email, givenName, familyName, "google");
            log.info("Social login (google) successful for email={}", email);
            return result;
        } catch (org.springframework.web.client.RestClientException ex) {
            log.warn("Error calling Google tokeninfo endpoint: {}", ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public SocialLoginResult loginWithFacebookAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalArgumentException("Missing Facebook access_token");
        }
        try {
            Map<String, Object> resp = restClient
                    .get()
                    .uri(URI.create("https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken))
                    .retrieve()
                    .body(Map.class);
            if (resp == null || resp.get("email") == null) {
                log.warn("Facebook token validation failed: response={}, token_hint={}", resp, maskToken(accessToken));
                throw new IllegalArgumentException("Invalid Facebook token or missing email permission");
            }
            String email = String.valueOf(resp.get("email"));
            String name = String.valueOf(resp.getOrDefault("name", ""));
            String[] parts = name.split(" ", 2);
            String first = parts.length > 0 ? parts[0] : "";
            String last = parts.length > 1 ? parts[1] : "";
            SocialLoginResult result = linkUserAndIssueToken(email, first, last, "facebook");
            log.info("Social login (facebook) successful for email={}", email);
            return result;
        } catch (org.springframework.web.client.RestClientException ex) {
            log.warn("Error calling Facebook graph endpoint: {}", ex.getMessage());
            throw ex;
        }
    }

    private SocialLoginResult linkUserAndIssueToken(
            String email, String firstName, String lastName, String provider) {
        Optional<User> existingByEmail = userRepository.findByEmail(email);
        User user =
                existingByEmail.orElseGet(
                        () -> {
                            User u = new User();
                            u.setEmail(email);
                            u.setFirstName(firstName);
                            u.setLastName(lastName);
                            u.setUsername(generateUsername(email, provider));
                            // Set a strong random password that meets policy, though it won't be used for social login
                            u.setPassword(generateStrongPassword());
                            u.setRoles(Role.USER);
                            return userRepository.save(u);
                        });

        String token = securityService.authenticateByUsernameAndGenerateToken(user.getUsername());
        ResponseCookie cookie = securityService.buildJwtCookie(token);
        return SocialLoginResult.builder().token(token).cookie(cookie).user(user).build();
    }

    private String generateUsername(String email, String provider) {
        String base = (email != null && email.contains("@")) ? email.substring(0, email.indexOf('@')) : "user";
        base = base.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
        String suffix = "_" + provider + randomDigits(4);
        String candidate = base + suffix;
        int attempt = 0;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + suffix + randomDigits(2 + attempt);
            attempt++;
        }
        return candidate;
    }

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final SecureRandom RNG = new SecureRandom();

    private String generateStrongPassword() {
        StringBuilder sb = new StringBuilder();
        // Ensure at least one char from each category
        sb.append(randomChar(UPPER));
        sb.append(randomChar(LOWER));
        sb.append(randomChar(DIGITS));
        sb.append(randomChar(SPECIAL));
        // Fill remaining to meet minimum length 12
        String all = UPPER + LOWER + DIGITS + SPECIAL;
        while (sb.length() < 12) {
            sb.append(randomChar(all));
        }
        return sb.toString();
    }

    private char randomChar(String chars) {
        return chars.charAt(RNG.nextInt(chars.length()));
    }

    private String randomDigits(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(RNG.nextInt(10));
        return sb.toString();
    }

    /**
     * Masks a token for logging. Keeps only first and last 4 characters.
     */
    private String maskToken(String token) {
        if (token == null) return "null";
        int len = token.length();
        if (len <= 8) return "***";
        return token.substring(0, 4) + "..." + token.substring(len - 4);
    }
}
