package bg.reshavalnik.app.domain.entity.task;

import java.time.LocalDateTime;
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

    @NonNull private String ownerId;

    @NonNull private Section section;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NonNull private String fileId;

    private byte[] img;
}
