package bg.reshavalnik.app.domain.model.task;

import java.util.Map;
import java.util.List;
import lombok.*;

@Getter
@Setter
public class GeneratedTask {
    private String id;
    private String userId;
    private String task;
    private Map<String, Object> options;
    private String answer;
    private String hint;
    private String solution;
    private String imageBase64;
    private List<ImageDto> images;
}
