package bg.reshavalnik.app.domain.model.section;

import bg.reshavalnik.app.domain.enums.Grade;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class SectionModel {

    @NonNull private Grade grade;

    @NonNull private String taskName;

    @NonNull private String sectionName;

    @NonNull private String algorithm;

    @NonNull private Integer sectionNumber;
}
