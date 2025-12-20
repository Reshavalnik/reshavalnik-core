package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.section.SectionModel;
import bg.reshavalnik.app.domain.model.taskSection.TaskSectionModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamTaskResponseModel {
    private Grade grade;
    private TaskSectionModel taskSection;
    private SectionModel section;
    private List<ExamTaskDto> tasks;
}
