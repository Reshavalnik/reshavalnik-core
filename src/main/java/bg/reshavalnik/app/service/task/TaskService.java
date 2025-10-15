package bg.reshavalnik.app.service.task;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.TASK_ALREADY_EXISTS;
import static bg.reshavalnik.app.exceptions.message.ErrorMessage.TASK_NOT_FOUND;

import bg.reshavalnik.app.domain.entity.task.Task;
import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.task.*;
import bg.reshavalnik.app.exceptions.exeption.TaskExceptions;
import bg.reshavalnik.app.mapper.task.TaskMapper;
import bg.reshavalnik.app.repository.task.TaskRepository;
import bg.reshavalnik.app.service.script.ScriptService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    private final ScriptService scriptService;

    private final TaskMapper taskMapper;

    public TaskResponseModel createTask(
            @Valid TaskRequestModel model, String userId, MultipartFile file) throws IOException {
        log.info("Creating task with name: {}", model);
        taskRepository
                .findByTaskName(model.getTaskName())
                .ifPresent(
                        task -> {
                            throw new TaskExceptions(TASK_ALREADY_EXISTS);
                        });

        String fileId = scriptService.createTask(file);
        Task task = taskMapper.mapToTask(model);
        task.setCreatedAt(LocalDateTime.now());
        task.setOwnerId(userId);
        task.setFileId(fileId);
        return taskMapper.mapToTaskResponseModel(taskRepository.save(task));
    }

    public TaskResponseModel updateTask(TaskUpdateRequestModel model, String id, MultipartFile file)
            throws IOException {
        log.info("Updating task with id: {}", id);

        Task task = findTaskById(model.getId());
        taskMapper.updateFromDto(model, task);
        if (!task.getOwnerId().equals(id)) {
            task.setOwnerId(id);
        }
        task.setUpdatedAt(LocalDateTime.now());
        String fileId = scriptService.update(file, task.getFileId());
        task.setFileId(fileId);
        return taskMapper.mapToTaskResponseModel(taskRepository.save(task));
    }

    public void deleteTask(String taskId, String id) {
        log.info("Deleting task with id: {}", id);
        Task task = findTaskById(taskId);
        if (!task.getOwnerId().equals(id)) {
            task.setOwnerId(id);
        }
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.delete(task);
    }

    public TaskResponseModel getTaskById(String taskId) {
        log.info("Getting task with id: {}", taskId);
        Task task = findTaskById(taskId);
        return taskMapper.mapToTaskResponseModel(task);
    }

    public List<TaskResponseModel> getTasksByUser(String userId) {
        log.info("Getting tasks for user with id: {}", userId);
        // ToDo: do need check userId exists
        List<Task> tasks = getAllTaskByOwnerId(userId);
        return taskMapper.mapToTaskResponseModelList(tasks);
    }

    public List<TaskResponseModel> getMyTasks(String id) {
        log.info("Getting tasks for user with id: {}", id);
        List<Task> tasks = getAllTaskByOwnerId(id);
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

    public GeneratedResponseTask generateTaskWithCount(String taskId, Integer count) {
        try {
            TaskResponseModel taskResponseModel = getTaskById(taskId);
            GeneratedResponseTask response =
                    taskMapper.mapToGeneratedResponseTask(taskResponseModel);
            List<GeneratedTask> generatedTasks = new java.util.ArrayList<>();
            for (int i = 1; i <= count; i++) {
                String generatedResultTask = scriptService.generate(taskResponseModel.getFileId());
                generatedTasks.add(mapGeneratedTaskToTask(generatedResultTask));
            }
            response.setTasks(generatedTasks);
            return response;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private GeneratedTask mapGeneratedTaskToTask(String generatedTask) {

        // 0) normalize + drop leading "Exit code: N"
        String cleaned =
                generatedTask == null
                        ? ""
                        : generatedTask
                                .replaceFirst("(?s)^\\s*Exit\\s*code\\s*:\\s*\\d+\\s*", "")
                                .replace("\r\n", "\n")
                                .replace('\u00A0', ' ')
                                .trim();

        // 1) stable anchors
        Pattern patternOptionStart = Pattern.compile("(?m)^\\s*[AА]\\)");
        Pattern patternAnswerLabel =
                Pattern.compile("Отг(?:овор)?\\s*[:\\-]?", Pattern.UNICODE_CASE);
        Pattern patternHintLabel =
                Pattern.compile("Уп[ъу]тван(?:е|ия)\\s*[:\\-]?", Pattern.UNICODE_CASE);
        Pattern patternSolutionLabel = Pattern.compile("Решение\\s*[:\\-]?", Pattern.UNICODE_CASE);
        Pattern patternAnswerValue =
                Pattern.compile(
                        "Отг(?:овор)?\\s*[:\\-]?\\s*([A-Za-zА-Яа-я])", Pattern.UNICODE_CASE);

        int idxOptionStart = findFirst(patternOptionStart, cleaned);
        int idxAnswerLabel = findFirst(patternAnswerLabel, cleaned);
        int idxHintLabel = findFirst(patternHintLabel, cleaned);
        int idxSolutionLabel = findFirst(patternSolutionLabel, cleaned);

        if (idxOptionStart < 0 || idxAnswerLabel < 0 || idxHintLabel < 0 || idxSolutionLabel < 0) {
            return null;
        }

        // 2) label lengths to trim them away
        int lenAnswerLabel = matchedLenAt(patternAnswerLabel, cleaned, idxAnswerLabel);
        int lenHintLabel = matchedLenAt(patternHintLabel, cleaned, idxHintLabel);
        int lenSolutionLabel = matchedLenAt(patternSolutionLabel, cleaned, idxSolutionLabel);

        // 3) split sections
        String taskText = cleaned.substring(0, idxOptionStart).trim();
        String optionsBlock = cleaned.substring(idxOptionStart, idxAnswerLabel).trim();
        String hintText = cleaned.substring(idxHintLabel + lenHintLabel, idxSolutionLabel).trim();
        String solutionText = cleaned.substring(idxSolutionLabel + lenSolutionLabel).trim();

        // 4) pick the answer letter
        Matcher mAns = patternAnswerValue.matcher(cleaned.substring(idxAnswerLabel));
        String answerLetter = mAns.find() ? mAns.group(1).trim() : "";

        // 5) parse options -> LinkedHashMap<letter, text>
        Pattern patternOptionLine = Pattern.compile("(?m)^\\s*([AАБВГ])\\)\\s*(.+?)\\s*$");
        Matcher mo = patternOptionLine.matcher(optionsBlock);
        java.util.LinkedHashMap<String, String> optionsMap = new java.util.LinkedHashMap<>();
        while (mo.find()) {
            String key = mo.group(1);
            if ("A".equalsIgnoreCase(key)) key = "А"; // normalize latin 'A' -> cyrillic 'А'
            String value = mo.group(2).trim();
            optionsMap.put(key, value);
        }

        // 6) build result
        GeneratedTask gt = new GeneratedTask();
        gt.setTask(taskText);
        gt.setOptions(optionsMap);
        gt.setAnswer(answerLetter);
        gt.setHint(hintText);
        gt.setSolution(solutionText);
        return gt;
    }

    private static int findFirst(Pattern p, String s) {
        Matcher m = p.matcher(s);
        return m.find() ? m.start() : -1;
    }

    private static int matchedLenAt(Pattern p, String s, int start) {
        Matcher m = p.matcher(s);
        return (start >= 0 && m.find(start) && m.start() == start) ? (m.end() - m.start()) : 0;
    }

    private List<Task> getAllTaskByOwnerId(String userId) {
        return taskRepository
                .findByOwnerId(userId)
                .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
    }

    private Task findTaskById(String modelId) {
        return taskRepository
                .findById(modelId)
                .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
    }
}
