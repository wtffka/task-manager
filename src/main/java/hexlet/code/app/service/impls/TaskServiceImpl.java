package hexlet.code.app.service.impls;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Task createTask(TaskDto taskDto) {
        final Task task = new Task();
        setTaskData(taskDto, task);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(TaskDto taskDto, Long id) {
        Task taskToUpdate = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with that id not found"));
        setTaskData(taskDto, taskToUpdate);
        return taskRepository.save(taskToUpdate);
    }

    @Override
    public void deleteTask(Long id) {
        if (taskRepository.findById(id).get().getAuthor().getEmail().equals(userService.getCurrentUserName())) {
            final Task taskToDelete = taskRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Task with that id not found"));
            taskRepository.delete(taskToDelete);
        }
    }

    private void setTaskData(TaskDto taskDto, Task task) {
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
