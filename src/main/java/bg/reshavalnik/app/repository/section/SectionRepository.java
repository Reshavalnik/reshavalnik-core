package bg.reshavalnik.app.repository.section;

import bg.reshavalnik.app.domain.entity.task.Section;
import bg.reshavalnik.app.domain.enums.Grade;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SectionRepository extends MongoRepository<Section, String> {

    boolean existsBySectionName(String section);

    Section getById(String sectionId);

    List<Section> findAllByGrade(Grade grade);
}
