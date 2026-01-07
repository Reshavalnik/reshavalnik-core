package bg.reshavalnik.app.domain.model.task;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamTaskDto {
    private String id;
    private String userId;
    private String task;
    private Map<String, Object> options;
    private String hint;
    private String imageBase64;
    private List<ImageDto> images;
}
