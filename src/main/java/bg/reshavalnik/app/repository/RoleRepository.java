package bg.reshavalnik.app.repository;

import bg.reshavalnik.app.security.domain.ERole;
import bg.reshavalnik.app.security.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByName(ERole name);
}
