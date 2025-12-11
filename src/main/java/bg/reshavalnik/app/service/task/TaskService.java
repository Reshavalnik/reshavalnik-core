package bg.reshavalnik.app.service.task;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.*;

import bg.reshavalnik.app.domain.entity.pendingExam.PendingExam;
import bg.reshavalnik.app.domain.entity.resultExam.ResultExam;
import bg.reshavalnik.app.domain.entity.task.ExamTask;
import bg.reshavalnik.app.domain.entity.task.Section;
import bg.reshavalnik.app.domain.entity.task.Task;
import bg.reshavalnik.app.domain.enums.Grade;
import bg.reshavalnik.app.domain.model.exam.ExamTaskExistResponseModel;
import bg.reshavalnik.app.domain.model.resultExam.*;
import bg.reshavalnik.app.domain.model.task.*;
import bg.reshavalnik.app.domain.model.task.GeneratedTask;
import bg.reshavalnik.app.exceptions.exeption.TaskExceptions;
import bg.reshavalnik.app.mapper.resultExam.ResultExamMapper;
import bg.reshavalnik.app.mapper.task.TaskMapper;
import bg.reshavalnik.app.repository.ExamTaskRepository;
import bg.reshavalnik.app.repository.pendingExam.PendingExamRepository;
import bg.reshavalnik.app.repository.resultExam.ResultExamRepository;
import bg.reshavalnik.app.repository.section.SectionRepository;
import bg.reshavalnik.app.repository.task.TaskRepository;
import bg.reshavalnik.app.repository.user.UserRepository;
import bg.reshavalnik.app.security.domain.User;
import bg.reshavalnik.app.security.security.services.UserDetails;
import bg.reshavalnik.app.service.script.ScriptService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    private final ScriptService scriptService;

    private final TaskMapper taskMapper;

    private final SectionRepository sectionRepository;

    private final ExamTaskRepository examTaskRepository;

    private final ResultExamRepository resultExamRepository;

    private final ResultExamMapper resultExamMapper;

    private final PendingExamRepository pendingExamRepository;

    private final UserRepository userRepository;

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
                                : requestModel.getStudents().getFirst();
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

    public Section addSection(@NonNull String section) {
        if (sectionRepository.existsBySection(section)) {
            throw new TaskExceptions(SECTION_ALREADY_EXISTS);
        }
        Section newSection = new Section();
        newSection.setSection(section);
        return sectionRepository.save(newSection);
    }

    public Section getSection(@NonNull String sectionId) {
        return sectionRepository
                .findById(sectionId)
                .orElseThrow(() -> new TaskExceptions(SECTION_NOT_FOUND));
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public void deleteSection(String sectionId) {
        Section section =
                sectionRepository
                        .findById(sectionId)
                        .orElseThrow(() -> new TaskExceptions(SECTION_NOT_FOUND));
        sectionRepository.delete(section);
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
        resultExam.setSectionName(examTask.getSection().getSection());
        resultExam.setExamDate(LocalDateTime.now());
        resultExamRepository.save(resultExam);

        CheckResultExamDto checkResultExamDto = new CheckResultExamDto();
        checkResultExamDto.setTask(generatedTask.getTask());
        checkResultExamDto.setOptions(generatedTask.getOptions());
        checkResultExamDto.setAnswer(generatedTask.getAnswer());
        checkResultExamDto.setHint(generatedTask.getHint());
        checkResultExamDto.setSolution(generatedTask.getSolution());
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
}
