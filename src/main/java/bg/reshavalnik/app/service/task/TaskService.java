package bg.reshavalnik.app.service.task;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.*;

import bg.reshavalnik.app.domain.entity.pendingExam.PendingExam;
import bg.reshavalnik.app.domain.entity.resultExam.ResultExam;
import bg.reshavalnik.app.domain.entity.task.ExamTask;
import bg.reshavalnik.app.domain.entity.task.Section;
import bg.reshavalnik.app.domain.entity.task.Task;
import bg.reshavalnik.app.domain.entity.task.TaskSection;
import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.exam.ExamTaskExistResponseModel;
import bg.reshavalnik.app.domain.model.resultExam.*;
import bg.reshavalnik.app.domain.model.task.*;
import bg.reshavalnik.app.domain.model.task.GeneratedTask;
import bg.reshavalnik.app.domain.model.task.ImageDto;
import bg.reshavalnik.app.exceptions.exeption.TaskExceptions;
import bg.reshavalnik.app.mapper.resultExam.ResultExamMapper;
import bg.reshavalnik.app.mapper.task.TaskMapper;
import bg.reshavalnik.app.repository.ExamTaskRepository;
import bg.reshavalnik.app.repository.pendingExam.PendingExamRepository;
import bg.reshavalnik.app.repository.resultExam.ResultExamRepository;
import bg.reshavalnik.app.repository.task.TaskRepository;
import bg.reshavalnik.app.repository.user.UserRepository;
import bg.reshavalnik.app.security.domain.User;
import bg.reshavalnik.app.security.security.services.UserDetails;
import bg.reshavalnik.app.service.script.ScriptService;
import bg.reshavalnik.app.service.section.SectionService;
import bg.reshavalnik.app.service.taskSection.TaskSectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    private final ScriptService scriptService;

    private final TaskMapper taskMapper;

    private final ExamTaskRepository examTaskRepository;

    private final ResultExamRepository resultExamRepository;

    private final ResultExamMapper resultExamMapper;

    private final PendingExamRepository pendingExamRepository;

    private final UserRepository userRepository;

    private final SectionService sectionService;

    private final TaskSectionService taskSectionService;

    private final ObjectMapper objectMapper;

    @Transactional
    public TaskResponseModel createTask(
            @Valid TaskRequestModel model, String userId, MultipartFile file) throws IOException {
        log.info("Creating task with name: {}", model);

        taskRepository
                .findByTaskSection_TaskName(model.getTaskSectionModel().getTaskName())
                .ifPresent(
                        task -> {
                            throw new TaskExceptions(TASK_ALREADY_EXISTS);
                        });

        Section section = getSection(model);

        TaskSection taskSection = taskSectionService.getById(model.getTaskSectionModel().getId());
        if (taskSection == null) {
            taskSection = taskSectionService.save(model.getTaskSectionModel(), section.getId());
        }

        String fileId = scriptService.createTask(file);

        Task task = getTask(model, userId, section, taskSection, fileId);
        return taskMapper.mapToTaskResponseModel(taskRepository.save(task));
    }

    private Section getSection(TaskRequestModel model) {
        Section section =
                sectionService.findBySectionName(model.getSectionModel().getSectionName());

        if (section == null) {
            section =
                    sectionService.save(model.getSectionModel().getSectionName(), model.getGrade());
        }
        return section;
    }

    private static @NonNull Task getTask(
            TaskRequestModel model,
            String userId,
            Section section,
            TaskSection taskSection,
            String fileId) {
        Task task = new Task();
        task.setGrade(Grade.valueOf(model.getGrade().getName()));
        task.setSection(section);
        task.setTaskSection(taskSection);
        task.setAlgorithm(model.getAlgorithm());
        task.setCreatedAt(LocalDateTime.now());
        task.setOwnerId(userId);
        task.setFileId(fileId);
        return task;
    }

    public TaskResponseModel updateTask(TaskUpdateRequestModel model, String id, MultipartFile file)
            throws IOException {
        log.info("Updating task with id: {}", id);

        Task task = findTaskById(model.getId());

        if (!task.getOwnerId().equals(id)) {
            task.setOwnerId(id);
        }
        if (model.getGrade() != null) {
            task.setGrade(Grade.valueOf(model.getGrade().getName()));
        }
        if (model.getTask() != null) {
            TaskSection taskSection =
                    taskSectionService.getByTaskName(model.getTask().getTaskName());
            task.setTaskSection(taskSection);
        }
        if (model.getAlgorithm() != null) {
            task.setAlgorithm(model.getAlgorithm());
        }
        if (model.getSection() != null) {
            Section newSection = sectionService.findById(task.getTaskSection().getSectionId());
            task.setSection(newSection);
        }
        if (model.getFileId() != null) {
            task.setFileId(model.getFileId());
        }
        if (model.getImg() != null) {
            task.setImg(model.getImg());
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

    public ExamTaskResponseModel generateTaskWithCount(
            GeneratedTaskRequestModel requestModel, UserDetails userDetails) {
        try {
            TaskResponseModel taskResponseModel = getTaskById(requestModel.getTaskId());
            ExamTask examTask = taskMapper.mapExamTask(taskResponseModel);
            examTask.setId(null);
            examTask.setGeneratedByUserId(userDetails.getId());
            LocalDateTime now = LocalDateTime.now();
            examTask.setCreatedAt(now);
            List<GeneratedTask> generatedTasks = new java.util.ArrayList<>();
            List<String> generatedResultTask =
                    scriptService.generate(taskResponseModel.getFileId(), requestModel.getCount());
            for (int i = 0; i < generatedResultTask.size(); i++) {
                String userId =
                        requestModel.getStudents().size() == generatedResultTask.size()
                                ? requestModel.getStudents().get(i)
                                : userDetails.getId();
                generatedTasks.add(
                        mapGeneratedTaskToTask(
                                generatedResultTask.get(i),
                                userId,
                                i,
                                now.toString(),
                                examTask.getGeneratedByUserId()));
            }
            examTask.setTasks(generatedTasks);

            return taskMapper.mapToGeneratedTask(examTaskRepository.save(examTask));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void savePendingExam(String generatedId, String userId) {
        PendingExam pendingExam = new PendingExam();
        pendingExam.setId(generatedId);
        pendingExam.setUserId(userId);
        pendingExamRepository.save(pendingExam);
    }

    private GeneratedTask mapGeneratedTaskToTask(
            String generatedTask,
            String userId,
            int index,
            String timestamp,
            String generatedByUserId) {

        ParsedTaskOutput parsed = parseTaskPayload(objectMapper, generatedTask);
        if (parsed == null || parsed.payload == null) {
            log.warn("Failed to parse generated task JSON payload.");
            return null;
        }
        ScriptTaskPayload payload = parsed.payload;
        String runDir = parsed.runDir;

        // 6) build result
        GeneratedTask gt = new GeneratedTask();
        gt.setTask(payload.task);
        gt.setOptions(payload.options);
        gt.setAnswer(payload.answer);
        gt.setHint(payload.hint);
        gt.setSolution(payload.solution);
        gt.setImageBase64(payload.imageBase64);
        gt.setImages(normalizeLegacyImages(payload.imageBase64));
        gt.setTaskImages(normalizeImageUris(payload.images, payload.imageBase64, true, runDir));
        gt.setSolutionImages(normalizeImageUris(payload.images, null, false, runDir));
        gt.setUserId(userId);
        gt.setId(generateId(generatedByUserId, userId, timestamp, index));

        savePendingExam(gt.getId(), userId);
        return gt;
    }

    private String generateId(
            String generatedByUserId, String userId, String timestamp, int index) {
        return generatedByUserId
                .concat("_")
                .concat(userId)
                .concat("_")
                .concat(timestamp)
                .concat("_")
                .concat(String.valueOf(index));
    }

    public Section addSection(@NonNull String sectionName, Grade grade) {
        if (sectionService.existsBySectionName(sectionName)) {
            throw new TaskExceptions(SECTION_ALREADY_EXISTS);
        }
        return sectionService.save(sectionName, grade);
    }

    public Section getSection(@NonNull String sectionId) {
        return sectionService.findById(sectionId);
    }

    public List<Section> getAllSections(Grade grade) {
        return sectionService.getAllSections(grade);
    }

    public void deleteSection(String sectionId) {
        sectionService.delete(sectionId);
    }

    static ParsedTaskOutput parseTaskPayload(ObjectMapper mapper, String output) {
        String cleaned =
                output == null
                        ? ""
                        : output.replaceFirst("(?s)^\\s*Exit\\s*code\\s*:\\s*\\d+\\s*", "")
                                .replace("\r\n", "\n")
                                .replace('\u00A0', ' ')
                                .trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        String[] lines = cleaned.split("\n");
        String runDir = null;
        for (String line : lines) {
            if (line.startsWith("RUN_DIR:")) {
                runDir = line.substring("RUN_DIR:".length()).trim();
            }
        }
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.startsWith("{") && line.endsWith("}")) {
                try {
                    ScriptTaskPayload payload = mapper.readValue(line, ScriptTaskPayload.class);
                    return new ParsedTaskOutput(payload, runDir);
                } catch (IOException ignore) {
                }
            }
        }
        return null;
    }

    private static List<ImageDto> normalizeLegacyImages(String imageBase64) {
        if (imageBase64 == null || imageBase64.isBlank()) {
            return java.util.Collections.emptyList();
        }
        ImageDto image = new ImageDto();
        image.setKind("TASK");
        image.setMime("image/png");
        image.setBase64(imageBase64);
        return java.util.List.of(image);
    }

    private static List<String> normalizeImageUris(
            ScriptTaskImages images, String imageBase64, boolean useTaskImages, String runDir) {
        List<String> uris = new ArrayList<>();
        if (images != null) {
            if (useTaskImages && images.task != null) {
                uris.addAll(toDataUris(images.task, runDir));
            }
            if (!useTaskImages && images.solution != null) {
                uris.addAll(toDataUris(images.solution, runDir));
            }
        }
        if (useTaskImages && uris.isEmpty()) {
            String legacy = toDataUriFromBase64(imageBase64, "image/png");
            if (!legacy.isEmpty()) {
                uris.add(legacy);
            }
        }
        return uris;
    }

    private static List<String> toDataUris(List<String> values, String runDir) {
        List<String> uris = new ArrayList<>();
        if (values == null || values.isEmpty()) {
            return uris;
        }

        Path base = runDir == null || runDir.isBlank() ? null : Path.of(runDir);

        for (String raw : values) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            if (raw.startsWith("data:image/")) {
                uris.add(raw);
                continue;
            }

            String cleaned = raw.startsWith("/") ? raw.substring(1) : raw;
            Path p = Path.of(cleaned);
            Path resolved = p.isAbsolute() || base == null ? p : base.resolve(p).normalize();

            try {
                if (!Files.exists(resolved)) {
                    LOGGER.warn("Image missing: {}", resolved);
                    continue;
                }

                byte[] bytes = Files.readAllBytes(resolved);
                String mime = guessMime(resolved);
                String b64 = Base64.getEncoder().encodeToString(bytes);
                uris.add("data:" + mime + ";base64," + b64);
            } catch (Exception e) {
                LOGGER.warn("Failed to read image {}: {}", resolved, e.getMessage());
            }
        }

        return uris;
    }

    private static String guessMime(Path p) {
        String name = p.getFileName().toString().toLowerCase();
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }

    //  private static List<String> toDataUris(List<String> paths, String runDir) {
    //        if (paths == null || paths.isEmpty()) {
    //            return java.util.Collections.emptyList();
    //        }
    //        List<String> uris = new ArrayList<>();
    //        for (String pathValue : paths) {
    //            String dataUri = toDataUriFromPath(pathValue, runDir);
    //            if (!dataUri.isEmpty()) {
    //                uris.add(dataUri);
    //            }
    //        }
    //        return uris;
    //    }

    private static String toDataUriFromPath(String pathValue, String runDir) {
        if (pathValue == null || pathValue.isBlank()) {
            return "";
        }
        try {
            String normalized = pathValue;
            if (normalized.startsWith("/")) {
                normalized = normalized.substring(1);
            }
            Path path = Paths.get(normalized);
            if (!path.isAbsolute() && runDir != null && !runDir.isBlank()) {
                path = Paths.get(runDir).resolve(path).normalize();
            }
            if (!Files.exists(path)) {
                LOGGER.warn("Image path does not exist: {}", path);
                return "";
            }
            byte[] bytes = Files.readAllBytes(path);
            String mime = guessMime(pathValue);
            String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
            return "data:" + mime + ";base64," + base64;
        } catch (Exception e) {
            LOGGER.warn("Failed to read image path {}: {}", pathValue, e.getMessage());
            return "";
        }
    }

    private static String toDataUriFromBase64(String base64, String mime) {
        if (base64 == null || base64.isBlank()) {
            return "";
        }
        return "data:" + mime + ";base64," + base64;
    }

    private static String guessMime(String pathValue) {
        String lower = pathValue.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "image/png";
    }

    static class ScriptTaskPayload {
        public String task;
        public java.util.Map<String, Object> options;
        public String answer;
        public String hint;
        public String solution;
        public String imageBase64;
        public ScriptTaskImages images;
    }

    static class ParsedTaskOutput {
        public final ScriptTaskPayload payload;
        public final String runDir;

        ParsedTaskOutput(ScriptTaskPayload payload, String runDir) {
            this.payload = payload;
            this.runDir = runDir;
        }
    }

    static class ScriptTaskImages {
        public java.util.List<String> task;
        public java.util.List<String> solution;
    }

    private List<Task> getAllTaskByOwnerId(String userId) {
        return taskRepository
                .findByOwnerId(userId)
                .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
    }

    private Task findTaskById(String taskId) {
        return taskRepository
                .findById(taskId)
                .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
    }

    public ExamTaskExistResponseModel getByExamExistTaskId(String taskId) {
        log.info("Getting exam task with id: {}", taskId);
        ExamTask examTask =
                examTaskRepository
                        .findById(taskId)
                        .orElseThrow(() -> new TaskExceptions(THERE_IS_NO_SUCH_TASK_FOR_THIS_USER));
        return taskMapper.mapToExamTaskExistResponseModel(examTask);
    }

    public List<ExamTaskExistResponseModel> getAllExamExist(String id) {
        log.info("Getting all exam tasks for user with id: {}", id);
        List<ExamTask> examTasks =
                examTaskRepository
                        .findAllByGeneratedByUserId(id)
                        .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
        return taskMapper.mapToExamExistResponseModelList(examTasks);
    }

    public CheckResultExamDto checkResultExam(
            String examId, String taskExamId, String answer, String userId) {
        log.info("Check exam result by exam ID: {}", examId);
        ExamTask examTask =
                examTaskRepository
                        .findById(taskExamId)
                        .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
        GeneratedTask generatedTask =
                examTask.getTasks().stream()
                        .filter(f -> f.getId().equals(examId))
                        .findFirst()
                        .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
        if (!userId.equals(generatedTask.getUserId())) {
            throw new TaskExceptions(SECTION_ALREADY_EXISTS);
        }
        boolean result = generatedTask.getAnswer().equalsIgnoreCase(answer);
        ResultExam resultExam = taskMapper.mapToResultExam(examTask);
        resultExam.setId(null);
        resultExam.setExamId(examId);
        resultExam.setUserId(userId);
        resultExam.setResult(result);
        resultExam.setUserAnswer(answer);
        resultExam.setSectionName(examTask.getSection().getSectionName());
        resultExam.setExamDate(LocalDateTime.now());
        resultExamRepository.save(resultExam);

        CheckResultExamDto checkResultExamDto = new CheckResultExamDto();
        checkResultExamDto.setTask(generatedTask.getTask());
        checkResultExamDto.setOptions(generatedTask.getOptions());
        checkResultExamDto.setAnswer(generatedTask.getAnswer());
        checkResultExamDto.setHint(generatedTask.getHint());
        checkResultExamDto.setSolution(generatedTask.getSolution());
        checkResultExamDto.setImageBase64(generatedTask.getImageBase64());
        checkResultExamDto.setImages(generatedTask.getImages());
        checkResultExamDto.setTaskImages(generatedTask.getTaskImages());
        checkResultExamDto.setSolutionImages(generatedTask.getSolutionImages());
        checkResultExamDto.setResult(result);

        removePendingExam(examId, userId);

        return checkResultExamDto;
    }

    private void removePendingExam(String examId, String userId) {
        PendingExam pendingExam =
                pendingExamRepository
                        .findById(examId)
                        .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
        pendingExamRepository.delete(pendingExam);
    }

    public List<ResultExamDto> getAllResultExamByUser(String userId) {
        return resultExamMapper.mapToResultExamDtos(
                resultExamRepository
                        .getAllByUserId(userId)
                        .orElseThrow(() -> new TaskExceptions(RESULT_EXAM_DOES_NOT_EXIST)));
    }

    public GeneratedTaskResponse fetchPendingExam(String userId) {
        PendingExam pendingExam =
                pendingExamRepository
                        .findByUserId(userId)
                        .orElseThrow(() -> new TaskExceptions(THERE_IS_NOT_EXIST_PENDING_EXAM));

        GeneratedTask gt =
                examTaskRepository
                        .findByTasksId(pendingExam.getId())
                        .map(
                                examTask ->
                                        examTask.getTasks().stream()
                                                .filter(t -> userId.equals(t.getUserId()))
                                                .findFirst()
                                                .orElseThrow(
                                                        () -> new TaskExceptions(TASK_NOT_FOUND)))
                        .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));

        return taskMapper.mapToGeneratedTaskDto(gt);
    }

    public FinishExamResult finishExam(String taskId, String userId) {
        ExamTask examTask =
                examTaskRepository
                        .findByIdAndGeneratedByUserId(taskId, userId)
                        .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));

        FinishExamResult finishExamResult = new FinishExamResult();

        for (GeneratedTask task : examTask.getTasks()) {
            User user =
                    userRepository
                            .findById(task.getUserId())
                            .orElseThrow(() -> new TaskExceptions(USER_NOT_FOUND));
            Optional<PendingExam> pendingExamOpt =
                    pendingExamRepository.findByUserId(task.getUserId());
            if (pendingExamOpt.isPresent() && pendingExamOpt.get().getId() != null) {
                toUncompletedUsers(user, finishExamResult, pendingExamOpt);
                pendingExamRepository.delete(pendingExamOpt.get());
                continue;
            }

            ResultExam resultExam =
                    resultExamRepository
                            .findByUserId(task.getUserId())
                            .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
            toCompletedUsers(user, finishExamResult, resultExam);
        }

        return finishExamResult;
    }

    private void toCompletedUsers(
            User user, FinishExamResult finishExamResult, ResultExam resultExam) {
        CompletedUsersDto completedUser = new CompletedUsersDto();
        completedUser.setFirstName(user.getFirstName());
        completedUser.setLastName(user.getLastName());
        completedUser.setResult(resultExam.getResult());
        completedUser.setUserAnswer(resultExam.getUserAnswer());

        finishExamResult.getCompletedUsers().add(completedUser);
    }

    private void toUncompletedUsers(
            User user, FinishExamResult finishExamResult, Optional<PendingExam> pendingExamOpt) {
        UncompletedUsersDto uncompletedUsers = new UncompletedUsersDto();
        uncompletedUsers.setFirstName(user.getFirstName());
        uncompletedUsers.setLastName(user.getLastName());

        finishExamResult.getUncompletedUserDtos().add(uncompletedUsers);
    }

    public List<Task> getTaskBySection(String sectionId) {
        return taskRepository
                .findAllBySection_Id(sectionId)
                .orElseThrow(() -> new TaskExceptions(TASK_NOT_FOUND));
    }
}
