package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.section.SectionModel;
import bg.reshavalnik.app.domain.model.taskSection.TaskSectionModel;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskRequestModel {

    @NonNull private Grade grade;

    @NonNull private TaskSectionModel taskSectionModel;

    @NonNull private SectionModel sectionModel;

    @NonNull private String algorithm;

    private byte[] img;
}
