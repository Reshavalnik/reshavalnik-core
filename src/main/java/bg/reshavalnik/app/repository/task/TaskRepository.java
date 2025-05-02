package bg.reshavalnik.app.repository.task;

import bg.reshavalnik.app.domain.entity.task.Task;
import bg.reshavalnik.app.domain.enums.Grade;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    Optional<Task> findByTaskName(String taskName);

    Optional<List<Task>> findByUserId(String userId);

    Optional<List<Task>> findByGrade(@NonNull Grade grade);
}
