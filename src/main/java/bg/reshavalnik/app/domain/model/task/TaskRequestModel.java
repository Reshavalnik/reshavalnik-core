package bg.reshavalnik.app.domain.model.task;

import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.section.SectionModel;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskRequestModel {

    @NonNull private Grade grade;

    @NonNull private String taskName;

    @NonNull private String algorithm;

    @NonNull private SectionModel section;

    private byte[] img;
}
