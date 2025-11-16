package bg.reshavalnik.app.repository.resultExam;

import bg.reshavalnik.app.domain.entity.resultExam.ResultExam;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultExamRepository extends MongoRepository<ResultExam, String> {
    Optional<List<ResultExam>> getAllByUserId(String userId);

    Optional<ResultExam> findByUserId(String userId);
}
