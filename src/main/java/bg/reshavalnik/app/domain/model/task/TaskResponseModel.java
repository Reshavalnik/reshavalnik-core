package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.section.SectionModel;
import bg.reshavalnik.app.domain.model.taskSection.TaskSectionModel;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskResponseModel {

    @Id private String id;

    private String ownerId;

    private SectionModel section;

    private Grade grade;

    private TaskSectionModel taskSection;

    private String algorithm;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String fileId;

    private byte[] img;
}
