package bg.reshavalnik.app.domain.model.section;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class SectionModel {

    @NonNull private String id;

    @NonNull private String sectionName;
}
