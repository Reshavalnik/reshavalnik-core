package bg.reshavalnik.app.domain.model.resultExam;

import bg.reshavalnik.app.domain.model.task.ImageDto;
import java.util.List;
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
    private String imageBase64;
    private List<ImageDto> images;
    private List<String> taskImages;
    private List<String> solutionImages;
    private boolean result;
}
