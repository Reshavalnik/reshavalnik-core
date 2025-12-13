package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.model.section.SectionModel;
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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String fileId;

    private byte[] img;
}
