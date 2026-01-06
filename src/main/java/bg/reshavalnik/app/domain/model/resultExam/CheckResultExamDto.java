package bg.reshavalnik.app.domain.model.resultExam;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckResultExamDto {
    private String task;
    private Map<String, Object> options;
    private String answer;
    private String hint;
    private String solution;
    private boolean result;
}
