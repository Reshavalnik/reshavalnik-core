package bg.reshavalnik.app;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@Import(ReshavalnikApplicationTests.PlaceholderIgnoreConfig.class)
class ReshavalnikApplicationTests {

    @MockitoBean GridFsTemplate gridFsTemplate;

    @Configuration
    static class PlaceholderIgnoreConfig {
        @Bean
        static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            var c = new PropertySourcesPlaceholderConfigurer();
            c.setIgnoreUnresolvablePlaceholders(true); // <-- ключът: не гърми на липсващи ${...}
            return c;
        }
    }

    @org.junit.jupiter.api.Test
    void contextLoads() {}
}
