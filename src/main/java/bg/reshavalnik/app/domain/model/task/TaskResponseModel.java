package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskResponseModel {

    private String id;

    private Grade grade;

    private String taskName;

    private String description;

    private String algorithm;

    private List<Character> possibleOptions;

    private String additionalInfo;

    private String example;

    private String filename;

    private byte[] img;
}
