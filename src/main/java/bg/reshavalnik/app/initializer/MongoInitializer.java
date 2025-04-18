package bg.reshavalnik.app.initializer;

import bg.reshavalnik.app.repository.RoleRepository;
import bg.reshavalnik.app.repository.UserRepository;
import bg.reshavalnik.app.security.domain.ERole;
import bg.reshavalnik.app.security.domain.Role;
import bg.reshavalnik.app.security.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;


@Data
@AllArgsConstructor
@Component
public class MongoInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(MongoInitializer.class);

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing MongoDB");
        createRoles();
    }

    private void createRoles() {
        createAdmin();
        creatUser();
    }

    private void createAdmin() {
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            Role role = new Role();
            role.setName(ERole.ROLE_ADMIN);
            roleRepository.save(role);
            log.info("Admin role created");

            User user = new User();
            user.setUsername("admin");
            user.setPassword(encoder.encode("Test@123"));
            user.setFirstName("Admin");
            user.setLastName("Adminov");
            user.setNickname("nickname");
            user.setPhone("0888888888");
            user.setRoles(Set.of(role));
            mongoTemplate.save(user);
            log.info("Admin user created");
        }
        log.info("Admin role already exists");
    }

    private void creatUser() {
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            Role role = new Role();
            role.setName(ERole.ROLE_USER);
            roleRepository.save(role);
            log.info("User role created");
        }
        log.info("User role already exists");
    }
}
