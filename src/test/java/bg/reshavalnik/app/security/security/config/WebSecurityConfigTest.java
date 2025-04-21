package bg.reshavalnik.app.security.security.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import bg.reshavalnik.app.security.config.WebSecurityConfig;
import bg.reshavalnik.app.security.security.jwt.AuthEntryPointJwt;
import bg.reshavalnik.app.security.security.services.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class WebSecurityConfigTest {

    private WebSecurityConfig config;

    @BeforeEach
    void setUp() {
        config = new WebSecurityConfig();

        ReflectionTestUtils.setField(config, "userDetailsService", mock(UserDetailsService.class));
        ReflectionTestUtils.setField(config, "unauthorizedHandler", mock(AuthEntryPointJwt.class));
    }

    @Test
    void daoAuthenticationProviderBean_ShouldUseBCryptPasswordEncoder() {
        DaoAuthenticationProvider provider = config.authenticationProvider();

        PasswordEncoder encoder =
                (PasswordEncoder) ReflectionTestUtils.getField(provider, "passwordEncoder");

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
}
