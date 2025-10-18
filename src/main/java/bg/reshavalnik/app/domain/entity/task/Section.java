package bg.reshavalnik.app.domain.entity.task;

import lombok.*;
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

    private String section;
}
