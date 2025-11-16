package bg.reshavalnik.app.domain.entity.pendingExam;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "pendingExam")
public class PendingExam {
    @Id private String id;
    private String userId;
}
