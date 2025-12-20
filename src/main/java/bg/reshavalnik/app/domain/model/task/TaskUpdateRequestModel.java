package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.section.SectionModel;
import bg.reshavalnik.app.domain.model.taskSection.TaskSectionModel;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskUpdateRequestModel {

    private String id;

    private String ownerId;

    private Grade grade;

    private TaskSectionModel task;

    private String algorithm;

    private SectionModel section;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String fileId;

    private byte[] img;
}
