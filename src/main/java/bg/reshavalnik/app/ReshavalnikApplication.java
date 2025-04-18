package bg.reshavalnik.app;

import bg.reshavalnik.app.security.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(SwaggerConfig.class)
@SpringBootApplication
public class ReshavalnikApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReshavalnikApplication.class, args);
    }
}
