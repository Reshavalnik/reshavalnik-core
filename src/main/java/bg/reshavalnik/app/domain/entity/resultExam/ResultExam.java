package bg.reshavalnik.app.domain.entity.resultExam;

import bg.reshavalnik.app.domain.enums.Grade;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "resultExam")
public class ResultExam {
    @Id private String id;
    private String userId;
    private Grade grade;
    private String taskName;
    private String sectionName;
    private Boolean result;
    private LocalDateTime examDate;
}
