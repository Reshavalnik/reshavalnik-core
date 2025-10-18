package bg.reshavalnik.app.repository.section;

import bg.reshavalnik.app.domain.entity.task.Section;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SectionRepository extends MongoRepository<Section, String> {

    boolean existsBySection(String section);
}
