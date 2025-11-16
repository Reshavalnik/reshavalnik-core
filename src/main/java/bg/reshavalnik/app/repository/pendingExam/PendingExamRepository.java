package bg.reshavalnik.app.repository.pendingExam;

import bg.reshavalnik.app.domain.entity.pendingExam.PendingExam;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingExamRepository extends MongoRepository<PendingExam, String> {

    Optional<PendingExam> findByUserId(String userId);
}
