package bg.reshavalnik.app.domain.entity.task;

import bg.reshavalnik.app.domain.enums.Grade;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "section")
public class Section {

    @Id private String id;

    @UniqueElements
    private String sectionName;

    private Grade grade;
}
