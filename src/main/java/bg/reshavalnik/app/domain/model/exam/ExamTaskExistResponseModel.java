package bg.reshavalnik.app.domain.model.exam;

import bg.reshavalnik.app.domain.entity.task.Section;
import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.task.GeneratedTask;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamTaskExistResponseModel {
    private String id;
    private String generatedByUserId;
    private Grade grade;
    private String taskName;
    private Section section;
    private List<GeneratedTask> tasks;
    private LocalDateTime createdAt;
}
