package hexlet.code.controllers;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.impls.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.validation.Valid;
import java.util.NoSuchElementException;

import static hexlet.code.utils.AppConstants.BASE_URL_FOR_TASK_CONTROLLER;
import static hexlet.code.utils.AppConstants.DELETE_TASK_SUCCESSFUL;
import static hexlet.code.utils.AppConstants.DELETE_TASK_UNSUCCESSFUL;
import static hexlet.code.utils.AppConstants.ID;

@RestController
@RequestMapping(BASE_URL_FOR_TASK_CONTROLLER)
public class TaskController {

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    @Operation(summary = "Get list of all Tasks")
    @ApiResponse(responseCode = "200", description = "List of all Tasks", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))
    public Iterable<Task> getTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @GetMapping(ID)
    @Operation(summary = "Get Task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public Task getTask(@Parameter(description = "Task Id to show") @PathVariable Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Task with that ID not found"));
    }

    @PostMapping
    @Operation(summary = "Creating new Task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "422", description = "Data validation failed")
    })
    public Task createTask(@Parameter(description = "Data to create Task") @RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping(ID)
    @Operation(summary = "Update Task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "422", description = "Data validation failed")
    })
    public Task updateTask(@Parameter(description = "Data to update task") @RequestBody @Valid TaskDto taskDto,
                           @Parameter(description = "Task id to update") @PathVariable Long id) {
        return taskService.updateTask(taskDto, id);
    }

    @DeleteMapping(ID)
    @Operation(summary = "Delete task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted"),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "403", description = "Operation available only for owner")
    })
    public String deleteTask(@Parameter(description = "Task id to delete") @PathVariable Long id) {
        int tasksBeforeDeleteAttempt = taskRepository.findAll().size();
        taskService.deleteTask(id);
        if (tasksBeforeDeleteAttempt != taskRepository.findAll().size()) {
            return DELETE_TASK_SUCCESSFUL;
        }
        return DELETE_TASK_UNSUCCESSFUL;
    }
}
