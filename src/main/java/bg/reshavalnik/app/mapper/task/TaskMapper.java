package bg.reshavalnik.app.mapper.task;

import bg.reshavalnik.app.domain.entity.task.Task;
import bg.reshavalnik.app.domain.model.task.TaskRequestModel;
import bg.reshavalnik.app.domain.model.task.TaskResponseModel;
import bg.reshavalnik.app.domain.model.task.TaskUpdateRequestModel;
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
}
