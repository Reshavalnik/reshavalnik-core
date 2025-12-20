package bg.reshavalnik.app.service.taskSection;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.TASK_SECTION_NOT_FOUND;

import bg.reshavalnik.app.domain.entity.task.TaskSection;
import bg.reshavalnik.app.domain.model.taskSection.TaskSectionModel;
import bg.reshavalnik.app.exceptions.exeption.TaskExceptions;
import bg.reshavalnik.app.repository.taskSection.TaskSectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskSectionService {

    private final TaskSectionRepository taskSectionRepository;

    public TaskSection save(TaskSectionModel model, String sectionId) {
        TaskSection taskSection = new TaskSection();
        taskSection.setTaskName(model.getTaskName());
        taskSection.setSectionId(sectionId);
        return taskSectionRepository.save(taskSection);
    }

    public TaskSection getById(String id) {
        return taskSectionRepository.getById(id);
    }

    public TaskSection getByTaskName(String taskName) {
        return taskSectionRepository
                .getByTaskName(taskName)
                .orElseThrow(() -> new TaskExceptions(TASK_SECTION_NOT_FOUND));
    }
}
