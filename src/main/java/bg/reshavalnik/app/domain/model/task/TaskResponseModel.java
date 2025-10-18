package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.entity.task.Section;
import bg.reshavalnik.app.domain.enums.Grade;
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

    private Grade grade;

    private String taskName;

    private String algorithm;

    private Section section;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String fileId;

    private byte[] img;
}
