package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskUpdateRequestModel {

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
