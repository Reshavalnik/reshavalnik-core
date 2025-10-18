package bg.reshavalnik.app.repository;

import bg.reshavalnik.app.domain.entity.task.ExamTask;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExamTaskRepository extends MongoRepository<ExamTask, String> {
    Optional<List<ExamTask>> findAllByGeneratedByUserId(String id);
}
