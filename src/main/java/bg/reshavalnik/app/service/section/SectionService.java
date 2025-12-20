package bg.reshavalnik.app.service.section;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.SECTION_NOT_FOUND;

import bg.reshavalnik.app.domain.entity.task.Section;
import bg.reshavalnik.app.exceptions.exeption.TaskExceptions;
import bg.reshavalnik.app.repository.section.SectionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class SectionService {

    private final SectionRepository sectionRepository;

    public Section save(String sectionName) {
        Section section = new Section();
        section.setSectionName(sectionName);
        return sectionRepository.save(section);
    }

    public Section getSectionById(String sectionId) {
        return sectionRepository.getById(sectionId);
    }

    public boolean existsBySectionName(String sectionName) {
        return sectionRepository.existsBySectionName(sectionName);
    }

    public Section findById(String sectionId) {
        return sectionRepository
                .findById(sectionId)
                .orElseThrow(() -> new TaskExceptions(SECTION_NOT_FOUND));
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public void delete(String sectionId) {
        Section section =
                sectionRepository
                        .findById(sectionId)
                        .orElseThrow(() -> new TaskExceptions(SECTION_NOT_FOUND));
        sectionRepository.delete(section);
    }
}
