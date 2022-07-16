package hexlet.code.service.impls;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserServiceImpl userService;

    private final UserRepository userRepository;

    private final LabelRepository labelRepository;

    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserServiceImpl userService, UserRepository userRepository,
                           LabelRepository labelRepository, TaskStatusRepository taskStatusRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.taskStatusRepository = taskStatusRepository;
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
        final TaskStatus taskStatus = taskStatusRepository.findById(taskDto.getTaskStatusId()).get();
        final User user = userService.getCurrentUser();
        task.setTaskStatus(taskStatus);
        task.setName(taskDto.getName());
        task.setAuthor(user);
        task.setDescription(taskDto.getDescription());
        task.setLabels(setLabelList(taskDto.getLabelIds()));
        if (taskDto.getExecutorId() != null) {
            final Long executorId = taskDto.getExecutorId();
            task.setExecutor(userRepository.findById(executorId).get());
        }

        return task;
    }

    private List<Label> setLabelList(Set<Long> labelIds) {
        if (labelIds == null) {
            return null;
        }
        List<Label> labelList = new ArrayList<>();
        for (Long labelId : labelIds) {
            final Label label = labelRepository.findById(labelId).
                    orElseThrow(() -> new NoSuchElementException("Label with that ID not found"));
            labelList.add(label);
        }
        return labelList;
    }
}
