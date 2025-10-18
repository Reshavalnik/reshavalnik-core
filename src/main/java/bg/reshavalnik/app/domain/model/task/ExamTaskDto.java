package bg.reshavalnik.app.domain.model.task;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamTaskDto {
    private String id;
    private String userId;
    private String task;
    private Map<String, String> options;
    private String hint;
}
