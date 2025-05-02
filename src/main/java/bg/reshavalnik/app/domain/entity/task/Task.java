package bg.reshavalnik.app.domain.entity.task;

import bg.reshavalnik.app.domain.enums.Grade;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "task")
public class Task {
    @Id private String id;

    @NonNull private String userId;

    @NonNull private Grade grade;

    @NonNull private String taskName;

    @NonNull private String description;

    @NonNull private String algorithm;

    @NonNull private List<Character> possibleOptions;

    private String additionalInfo;

    private String example;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String filename;

    private byte[] img;
}
