package bg.reshavalnik.app.mapper.resultExam;

import bg.reshavalnik.app.domain.entity.resultExam.ResultExam;
import bg.reshavalnik.app.domain.model.resultExam.ResultExamDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResultExamMapper {
    List<ResultExamDto> mapToResultExamDtos(List<ResultExam> resultExams);
}
