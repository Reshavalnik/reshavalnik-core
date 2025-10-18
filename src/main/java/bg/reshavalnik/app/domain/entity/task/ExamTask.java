package bg.reshavalnik.app.domain.entity.task;

import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.task.GeneratedTask;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "examTask")
public class ExamTask {
    private String id;
    private String generatedByUserId;
    private Grade grade;
    private String taskName;
    private Section section;
    private List<GeneratedTask> tasks;
    private LocalDateTime createdAt;
}
