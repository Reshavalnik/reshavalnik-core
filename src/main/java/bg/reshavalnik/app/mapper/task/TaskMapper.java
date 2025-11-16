package bg.reshavalnik.app.mapper.task;

import bg.reshavalnik.app.domain.entity.resultExam.ResultExam;
import bg.reshavalnik.app.domain.entity.task.ExamTask;
import bg.reshavalnik.app.domain.entity.task.Task;
import bg.reshavalnik.app.domain.model.exam.ExamTaskExistResponseModel;
import bg.reshavalnik.app.domain.model.task.*;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    Task mapToTask(TaskRequestModel taskRequestModel);

    void updateFromDto(TaskUpdateRequestModel dto, @MappingTarget Task entity);

    TaskResponseModel mapToTaskResponseModel(Task task);

    List<TaskResponseModel> mapToTaskResponseModelList(List<Task> tasks);

    ExamTask mapExamTask(TaskResponseModel taskResponseModel);

    ExamTaskResponseModel mapToGeneratedTask(ExamTask response);

    ExamTaskExistResponseModel mapToExamTaskExistResponseModel(ExamTask response);

    List<ExamTaskExistResponseModel> mapToExamExistResponseModelList(List<ExamTask> examTasks);

    ResultExam mapToResultExam(ExamTask examTask);

    GeneratedTaskResponse mapToGeneratedTaskDto(GeneratedTask gt);
}
