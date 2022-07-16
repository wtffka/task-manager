package hexlet.code.controllers;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.utils.AppConstants.ID;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER)
public class TaskStatusController {

    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskStatusController(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @GetMapping(ID)
    public TaskStatus getTaskStatus(@PathVariable Long id) {
        return taskStatusRepository.findById(id).
                orElseThrow(() -> new NoSuchElementException("TaskStatus with that ID not found"));
    }

    @GetMapping
    public List<TaskStatus> getTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto, @PathVariable Long id) {
        TaskStatus taskStatusToUpdate = taskStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TaskStatus with that id not found"));

        taskStatusToUpdate.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatusToUpdate);
    }

    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable Long id) {
        final TaskStatus taskStatusToDelete = taskStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TaskStatus with that id not found"));
        taskStatusRepository.delete(taskStatusToDelete);
    }
}
