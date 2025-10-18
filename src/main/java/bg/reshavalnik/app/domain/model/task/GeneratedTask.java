package bg.reshavalnik.app.domain.model.task;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratedTask {
    private String id;
    private String task;
    private Map<String, String> options;
    private String answer;
    private String hint;
    private String solution;
}
