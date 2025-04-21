package bg.reshavalnik.app.security.security.config;

import static org.junit.jupiter.api.Assertions.*;

import bg.reshavalnik.app.security.config.SecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(SecurityConfig.class);
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    @Test
    void encoderBean_ShouldBeBCryptPasswordEncoder() {
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
        assertNotNull(encoder, "PasswordEncoder bean трябва да съществува");
        assertTrue(
                encoder instanceof BCryptPasswordEncoder,
                "PasswordEncoder bean трябва да е инстанция на BCryptPasswordEncoder");
    }

    @Test
    void encode_And_Matches_ShouldWorkCorrectly() {
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
        String rawPassword = "mySecret123!";

        String encoded = encoder.encode(rawPassword);
        assertNotEquals(
                rawPassword,
                encoded,
                "Encoded password не трябва да бъде същият като raw password");

        assertTrue(
                encoder.matches(rawPassword, encoded),
                "matches() трябва да потвърди, че rawPassword съвпада с encoded value");

        assertFalse(
                encoder.matches("wrongPassword", encoded),
                "matches() трябва да върне false за несъответстващ raw password");
    }
}
