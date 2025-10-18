package bg.reshavalnik.app.domain.model.task;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratedTaskRequestModel {

    private String taskId;

    @Min(value = 1, message = "cannot be les then 1")
    private int count;

    private List<String> students;
}
