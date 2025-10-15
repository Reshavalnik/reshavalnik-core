package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskResponseModel {

    private String id;

    private String ownerId;

    private Grade grade;

    private String taskName;

    private String algorithm;

    private String textbookSection;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String fileId;

    private byte[] img;
}
