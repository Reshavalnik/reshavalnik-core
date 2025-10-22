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

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class SocialAuthController {

    private final SocialAuthService socialAuthService;

    public record SocialLoginRequest(@NotBlank String token, String redirectUri) {}

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
