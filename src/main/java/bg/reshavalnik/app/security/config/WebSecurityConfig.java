package bg.reshavalnik.app.security.config;

import bg.reshavalnik.app.security.security.jwt.AuthEntryPointJwt;
import bg.reshavalnik.app.security.security.jwt.AuthTokenFilter;
import bg.reshavalnik.app.security.security.services.UserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired private UserDetailsService userDetailsService;

    @Autowired private AuthEntryPointJwt unauthorizedHandler;

    //    @Value("${cors.allowedOrigins}")
    private List<String> allowedOrigins;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/auth/**")
                                        .permitAll()
                                        .requestMatchers("/api/task")
                                        .permitAll()
                                        .requestMatchers("/swagger-ui/**")
                                        .permitAll()
                                        .requestMatchers("/swagger-ui.html")
                                        .permitAll()
                                        .requestMatchers("/actuator/health", "/actuator/health/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(
                authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        http.logout(
                logout ->
                        logout.logoutUrl("/auth/logout")
                                .addLogoutHandler(new SecurityContextLogoutHandler())
                                .logoutSuccessHandler(
                                        (req, res, auth) -> {
                                            res.setStatus(HttpServletResponse.SC_OK);
                                        })
                                .permitAll());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cookie"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
