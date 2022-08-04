package hexlet.code.service.impl;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;


@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        return taskRepository.save(fromTaskDto(taskDto));
    }

    @Override
    public Task updateTask(TaskDto taskDto, Long id) {
        Task taskToUpdate = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with that id not found"));
        Task updatedTask = fromTaskDto(taskDto);

        updatedTask.setId(taskToUpdate.getId());
        updatedTask.setCreatedAt(taskToUpdate.getCreatedAt());

        return taskRepository.save(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        if (taskRepository.findById(id).get().getAuthor().getEmail().equals(userService.getCurrentEmail())) {
            final Task taskToDelete = taskRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Task with that id not found"));
            taskRepository.delete(taskToDelete);
        }
    }

    private Task fromTaskDto(TaskDto taskDto) {
        Task task = new Task();
        final User user = userService.getCurrentUser();

        final TaskStatus taskStatus = Optional.ofNullable(taskDto.getTaskStatusId())
                .map(TaskStatus::new)
                .orElse(null);

        final User executor = Optional.ofNullable(taskDto.getExecutorId())
                .map(User::new)
                .orElse(null);


        task.setTaskStatus(taskStatus);
        task.setName(taskDto.getName());
        task.setAuthor(user);
        task.setDescription(taskDto.getDescription());
        task.setLabels(setLabelList(taskDto.getLabelIds()));
        task.setExecutor(executor);

        return task;
    }

    private Set<Label> setLabelList(Set<Long> labelIds) {

        if (labelIds == null) {
            return null;
        }

        Set<Label> labelList = new HashSet<>();
        for (Long labelId : labelIds) {
            final Label label = Optional.ofNullable(labelId)
                    .map(Label::new)
                    .orElse(null);

            labelList.add(label);
        }

        return labelList;
    }
}
