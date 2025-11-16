package bg.reshavalnik.app.domain.model.resultExam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompletedUsersDto {
    private String firstName;
    private String lastName;
    private Boolean result;
    private String userAnswer;
}
