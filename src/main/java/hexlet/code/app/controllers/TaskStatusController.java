package hexlet.code.app.controllers;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.impls.TaskStatusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.ID;

@RestController
@RequestMapping(BASE_URL_FOR_TASK_STATUSES_CONTROLLER)
public class TaskStatusController {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusServiceImpl taskStatusService;

    @GetMapping(ID)
    public TaskStatus getTaskStatus(@PathVariable Long id) {
        return taskStatusRepository.findById(id).get();
    }

    @GetMapping("")
    public List<TaskStatus> getTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    @PostMapping("")
    public TaskStatus createTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.createTaskStatus(taskStatusDto);
    }

    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto, @PathVariable Long id) {
        return taskStatusService.updateTaskStatus(taskStatusDto, id);
    }

    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable Long id) {
        taskStatusService.deleteTaskStatus(id);
    }
}
