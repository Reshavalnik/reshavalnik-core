package bg.reshavalnik.app.domain.model.resultExam;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinishExamResult {
    private Set<UncompletedUsersDto> uncompletedUserDtos = new HashSet<>();
    private Set<CompletedUsersDto> completedUsers = new HashSet<>();
}
