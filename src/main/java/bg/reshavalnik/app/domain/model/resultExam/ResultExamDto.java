package bg.reshavalnik.app.domain.model.resultExam;

import bg.reshavalnik.app.domain.enums.Grade;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultExamDto {
    private String userId;
    private Grade grade;
    private String taskName;
    private String sectionName;
    private Boolean result;
    private LocalDateTime examDate;
}
