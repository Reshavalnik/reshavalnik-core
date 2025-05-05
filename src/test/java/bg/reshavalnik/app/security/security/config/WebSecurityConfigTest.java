package bg.reshavalnik.app.security.security.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import bg.reshavalnik.app.security.config.WebSecurityConfig;
import bg.reshavalnik.app.security.security.jwt.AuthEntryPointJwt;
import bg.reshavalnik.app.security.security.services.UserDetailsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

class WebSecurityConfigTest {

    private WebSecurityConfig config;

    @BeforeEach
    void setUp() {
        config = new WebSecurityConfig();
        ReflectionTestUtils.setField(config, "userDetailsService", mock(UserDetailsService.class));
        ReflectionTestUtils.setField(config, "unauthorizedHandler", mock(AuthEntryPointJwt.class));
        ReflectionTestUtils.setField(config, "allowedOrigins", List.of("*"));
    }

    @Test
    void daoAuthenticationProviderBean_ShouldUseBCryptPasswordEncoder() {
        DaoAuthenticationProvider provider = config.authenticationProvider();

        PasswordEncoder encoder =
                (PasswordEncoder) ReflectionTestUtils.getField(provider, "passwordEncoder");

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void corsConfigurationSource_ShouldAllowCookieHeader() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        UrlBasedCorsConfigurationSource urlSource = (UrlBasedCorsConfigurationSource) source;
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        CorsConfiguration corsConfig = urlSource.getCorsConfiguration(request);

        assertThat(corsConfig.getAllowedHeaders())
                .contains("Authorization", "Content-Type", "Cookie");
        assertThat(corsConfig.getAllowCredentials()).isTrue();
    }
}
