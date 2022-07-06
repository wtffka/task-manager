package hexlet.code.app.controllers;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.impls.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_TASK_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.DELETE_TASK_SUCCESSFUL;
import static hexlet.code.app.utils.AppConstants.DELETE_TASK_UNSUCCESSFUL;
import static hexlet.code.app.utils.AppConstants.ID;

@RestController
@RequestMapping(BASE_URL_FOR_TASK_CONTROLLER)
public class TaskController {

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @GetMapping(ID)
    public Task getTask(@PathVariable Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Task with that ID not found"));
    }

    @PostMapping
    public Task createTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping(ID)
    public Task updateTask(@RequestBody @Valid TaskDto taskDto, @PathVariable Long id) {
        return taskService.updateTask(taskDto, id);
    }

    @DeleteMapping(ID)
    public String deleteTask(@PathVariable Long id) {
        int tasksBeforeDeleteAttempt = taskRepository.findAll().size();
        taskService.deleteTask(id);
        if (tasksBeforeDeleteAttempt != taskRepository.findAll().size()) {
            return DELETE_TASK_SUCCESSFUL;
        }
        return DELETE_TASK_UNSUCCESSFUL;
    }
}
