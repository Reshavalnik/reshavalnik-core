package bg.reshavalnik.app.controller.task;

import bg.reshavalnik.app.domain.model.task.TaskRequestModel;
import bg.reshavalnik.app.domain.model.task.TaskUpdateRequestModel;
import bg.reshavalnik.app.security.security.services.UserDetails;
import bg.reshavalnik.app.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/task")
@AllArgsConstructor
@RestController
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<?> createTask(
            @Valid @RequestBody TaskRequestModel model,
            @AuthenticationPrincipal UserDetails userDetails) {

        return new ResponseEntity<>(
                taskService.createTask(model, userDetails.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateTask(
            @RequestBody TaskUpdateRequestModel model,
            @AuthenticationPrincipal UserDetails userDetails) {

        return new ResponseEntity<>(
                taskService.updateTask(model, userDetails.getId()), HttpStatus.OK);
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
}
