package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratedResponseTask {
    private Grade grade;
    private String taskName;
    private String textbookSection;
    private List<GeneratedTask> tasks;
}
