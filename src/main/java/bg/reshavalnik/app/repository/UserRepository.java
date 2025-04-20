package bg.reshavalnik.app.repository;

import bg.reshavalnik.app.security.domain.User;
import bg.reshavalnik.app.security.dto.projection.UserDetailProjection;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByUsername(String username);

    Optional<UserDetailProjection> getUserByUsername(String username);
}
