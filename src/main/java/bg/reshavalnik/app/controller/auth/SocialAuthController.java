package bg.reshavalnik.app.controller.auth;

import bg.reshavalnik.app.security.dto.response.AuthResponse;
import bg.reshavalnik.app.security.dto.response.UserProfile;
import bg.reshavalnik.app.service.security.SocialAuthService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST endpoints for social authentication flows (token-exchange and optional redirect).
 *
 * <p>The frontend obtains an identity token (e.g., Google id_token) directly from the
 * provider using its JavaScript SDK and sends it to the backend for verification.
 * Backend validates the token, finds or creates the local user by email, issues
 * a JWT and returns it via HttpOnly cookie. Optionally, a redirect URL is provided
 * through the X-Redirect-To header for client navigation.</p>
 */
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class SocialAuthController {

    private final SocialAuthService socialAuthService;

    public record SocialLoginRequest(@NotBlank String token, String redirectUri) {}

    /**
     * Token-exchange endpoint. Accepts a provider and a short-lived identity token from the client
     * (e.g., Google id_token) and returns a session via HttpOnly cookie. Optionally includes
     * an X-Redirect-To header to navigate the SPA to its callback page.
     */
    @PostMapping("/oauth2/login/{provider}")
    public ResponseEntity<AuthResponse> socialLogin(
            @PathVariable("provider") String provider, @RequestBody SocialLoginRequest body) {
        SocialAuthService.SocialLoginResult result = switch (provider.toLowerCase()) {
            case "google" -> socialAuthService.loginWithGoogleIdToken(body.token());
            case "facebook" -> socialAuthService.loginWithFacebookAccessToken(body.token());
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };

        UserProfile profile = UserProfile.builder()
                .id(result.getUser().getId())
                .username(result.getUser().getUsername())
                .email(result.getUser().getEmail())
                .firstName(result.getUser().getFirstName())
                .lastName(result.getUser().getLastName())
                .roles(result.getUser().getRoles())
                .build();

        ResponseEntity.BodyBuilder resp = ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.getCookie().toString());

        if (body.redirectUri() != null && !body.redirectUri().isBlank()) {
            // Some clients may prefer redirect; include redirect URL in response for convenience
            String redirect = UriComponentsBuilder.fromUriString(body.redirectUri())
                    .queryParam("token", result.getToken())
                    .build().toUriString();
            resp.header("X-Redirect-To", redirect);
        }

        return resp.body(AuthResponse.builder().tokenType("Bearer").user(profile).build());
    }

    /**
     * Optional redirect-based bridge that accepts tokens as query params and redirects to the SPA
     * with a JWT token attached. Prefer using the POST token-exchange endpoint from the frontend.
     */
    @GetMapping("/oauth2/authorize/{provider}")
    public ResponseEntity<Void> authorizeAndRedirect(
            @PathVariable("provider") String provider,
            @RequestParam(name = "redirect_uri") String redirectUri,
            @RequestParam(name = "id_token", required = false) String idToken,
            @RequestParam(name = "access_token", required = false) String accessToken,
            @RequestParam(name = "token", required = false) String genericToken) {
        SocialAuthService.SocialLoginResult result;
        String tokenToUse = genericToken;
        if (tokenToUse == null || tokenToUse.isBlank()) {
            if ("google".equalsIgnoreCase(provider)) {
                tokenToUse = idToken;
                result = socialAuthService.loginWithGoogleIdToken(tokenToUse);
            } else if ("facebook".equalsIgnoreCase(provider)) {
                tokenToUse = accessToken;
                result = socialAuthService.loginWithFacebookAccessToken(tokenToUse);
            } else {
                throw new IllegalArgumentException("Unsupported provider: " + provider);
            }
        } else {
            // If generic token was provided, try both (best-effort)
            result = "google".equalsIgnoreCase(provider)
                    ? socialAuthService.loginWithGoogleIdToken(tokenToUse)
                    : socialAuthService.loginWithFacebookAccessToken(tokenToUse);
        }

        String redirect = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", result.getToken())
                .build().toUriString();

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, redirect)
                .header(HttpHeaders.SET_COOKIE, result.getCookie().toString())
                .build();
    }
}
