package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.entity.task.Section;
import bg.reshavalnik.app.domain.enums.Grade;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamTaskResponseModel {
    private String id;
    private Grade grade;
    private LocalDateTime createdAt;
    private String taskName;
    private Section section;
    private List<ExamTaskDto> tasks;
}
