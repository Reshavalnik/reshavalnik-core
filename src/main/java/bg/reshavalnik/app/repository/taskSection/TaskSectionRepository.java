package bg.reshavalnik.app.repository.taskSection;

import bg.reshavalnik.app.domain.entity.task.TaskSection;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskSectionRepository extends MongoRepository<TaskSection, String> {

    TaskSection getById(String id);

    Optional<TaskSection> getByTaskName(String taskName);
}
