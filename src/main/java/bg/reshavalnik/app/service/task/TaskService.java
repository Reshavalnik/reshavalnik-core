package bg.reshavalnik.app.service.task;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.TASK_ALREADY_EXISTS;
import static bg.reshavalnik.app.exceptions.message.ErrorMessage.TASK_NOT_FOUND;

import bg.reshavalnik.app.domain.entity.task.Task;
import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.task.TaskRequestModel;
import bg.reshavalnik.app.domain.model.task.TaskResponseModel;
import bg.reshavalnik.app.domain.model.task.TaskUpdateRequestModel;
import bg.reshavalnik.app.exceptions.exeption.TaskExceptions;
import bg.reshavalnik.app.mapper.task.TaskMapper;
import bg.reshavalnik.app.repository.task.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskResponseModel createTask(TaskRequestModel model, String userId) {
        log.info("Creating task with name: {}", model);
        taskRepository
                .findByTaskName(model.getTaskName())
                .ifPresent(
                        task -> {
                            throw new TaskExceptions(TASK_ALREADY_EXISTS);
                        });
        Task task = taskMapper.mapToTask(model);
        task.setCreatedAt(LocalDateTime.now());
        task.setUserId(userId);
        return taskMapper.mapToTaskResponseModel(taskRepository.save(task));
    }

    public TaskResponseModel updateTask(TaskUpdateRequestModel model, String id) {
        log.info("Updating task with id: {}", id);
        Task task = findTaskById(model.getId());
        taskMapper.updateFromDto(model, task);
        if (!task.getUserId().equals(id)) {
            task.setUserId(id);
        }
        task.setUpdatedAt(LocalDateTime.now());
        return taskMapper.mapToTaskResponseModel(taskRepository.save(task));
    }

    public void deleteTask(String taskId, String id) {
        log.info("Deleting task with id: {}", id);
        Task task = findTaskById(taskId);
        if (!task.getUserId().equals(id)) {
            task.setUserId(id);
        }
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.delete(task);
    }

    public Object getTaskById(String taskId) {
        log.info("Getting task with id: {}", taskId);
        Task task = findTaskById(taskId);
        return taskMapper.mapToTaskResponseModel(task);
    }

    public List<TaskResponseModel> getTasksByUser(String userId) {
        log.info("Getting tasks for user with id: {}", userId);
        // ToDo: do need check userId exists
        List<Task> tasks = getAllTaskByUserId(userId);
        return taskMapper.mapToTaskResponseModelList(tasks);
    }

    public List<TaskResponseModel> getMyTasks(String id) {
        log.info("Getting tasks for user with id: {}", id);
        List<Task> tasks = getAllTaskByUserId(id);
        return taskMapper.mapToTaskResponseModelList(tasks);
    }

    public List<TaskResponseModel> getAllTasks() {
        log.info("Getting all tasks");
        List<Task> tasks = taskRepository.findAll();
        if (tasks.isEmpty()) {
            throw new TaskExceptions("No tasks found");
        }
        return taskMapper.mapToTaskResponseModelList(tasks);
    }

    public List<TaskResponseModel> getAllTasksByGrade(String grade) {
        Grade gradeEnum = Grade.fromLevel(Integer.parseInt(grade));

        log.info("Getting tasks for grade: {}", grade);
        List<Task> tasks =
                taskRepository
                        .findByGrade(Grade.valueOf(String.valueOf(gradeEnum)))
                        .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
        return taskMapper.mapToTaskResponseModelList(tasks);
    }

    private List<Task> getAllTaskByUserId(String userId) {
        return taskRepository
                .findByUserId(userId)
                .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
    }

    private Task findTaskById(String modelId) {
        return taskRepository
                .findById(modelId)
                .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
    }
}
