package bg.reshavalnik.app.domain.entity.task;

import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "taskSection")
public class TaskSection {

    @Id private String id;

    @UniqueElements private String taskName;

    private String sectionId;
}
