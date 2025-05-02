package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskRequestModel {

    @NonNull private String taskName;

    @NonNull private String description;

    @NonNull private String algorithm;

    @NonNull private List<Character> possibleOptions;

    @NonNull private Grade grade;

    private String additionalInfo;

    private String example;

    private String filename;

    private byte[] img;
}
