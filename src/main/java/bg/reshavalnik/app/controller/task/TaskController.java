package bg.reshavalnik.app.controller.task;

import bg.reshavalnik.app.domain.model.task.TaskRequestModel;
import bg.reshavalnik.app.domain.model.task.TaskUpdateRequestModel;
import bg.reshavalnik.app.security.security.services.UserDetails;
import bg.reshavalnik.app.service.script.ScriptService;
import bg.reshavalnik.app.service.task.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Min;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/task")
@AllArgsConstructor
@RestController
@Validated
public class TaskController {

    private final TaskService taskService;

    private final ScriptService svc;

    private final ObjectMapper objectMapper;

    @PostMapping(
            path = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTask(
            @RequestPart("model") String modelJson,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails)
            throws IOException {

        TaskRequestModel model = objectMapper.readValue(modelJson, TaskRequestModel.class);
        return new ResponseEntity<>(
                taskService.createTask(model, userDetails.getId(), file), HttpStatus.CREATED);
    }

    @PostMapping(
            path = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTask(
            @RequestPart("model") String modelJson,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails)
            throws IOException {
        TaskUpdateRequestModel model =
                objectMapper.readValue(modelJson, TaskUpdateRequestModel.class);
        return new ResponseEntity<>(
                taskService.updateTask(model, userDetails.getId(), file), HttpStatus.OK);
    }

    @GetMapping("/delete")
    public ResponseEntity<?> deleteTask(
            @RequestParam("task-id") String taskId,
            @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTask(taskId, userDetails.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<?> getTaskById(@RequestParam("task-id") String taskId) {
        return new ResponseEntity<>(taskService.getTaskById(taskId), HttpStatus.OK);
    }

    @GetMapping("/get-by-user")
    public ResponseEntity<?> getTasksByUser(@RequestParam("user-id") String userId) {
        return new ResponseEntity<>(taskService.getTasksByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/get-my-tasks")
    public ResponseEntity<?> getMyTasks(@AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(taskService.getMyTasks(userDetails.getId()), HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllTasks() {
        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }

    @GetMapping("/get-all-by-grade")
    public ResponseEntity<?> getAllTasksByGrade(@RequestParam("grade") String grade) {
        return new ResponseEntity<>(taskService.getAllTasksByGrade(grade), HttpStatus.OK);
    }

    //    @PostMapping(path = "/create-task", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //    public ResponseEntity<String> createTask(@RequestParam("file") MultipartFile file)
    //            throws Exception {
    //        String id = svc.createTask(file);
    //        return ResponseEntity.ok(id);
    //    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateTask(
            @RequestParam("taskId") String taskId,
            @RequestParam("count") @Min(value = 1, message = "cannot be les then 1") int count) {
        return ResponseEntity.ok(taskService.generateTaskWithCount(taskId, count));
    }

    @PostMapping("/add-section")
    public ResponseEntity<?> addSection(@RequestParam("section") String section) {
        return ResponseEntity.ok(taskService.addSection(section));
    }

    @PostMapping("/get-section")
    public ResponseEntity<?> getSection(@RequestParam("sectionId") String sectionId) {
        return ResponseEntity.ok(taskService.getSection(sectionId));
    }

    @GetMapping("/get-all-sections")
    public ResponseEntity<?> getAllSections() {
        return ResponseEntity.ok(taskService.getAllSections());
    }

    @DeleteMapping("/delete-section")
    public ResponseEntity<?> deleteSection(@RequestParam("sectionId") String sectionId) {
        taskService.deleteSection(sectionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
