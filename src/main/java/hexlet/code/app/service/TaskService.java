package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;

public interface TaskService {

    Task createTask(TaskDto taskDto);

    Task updateTask(TaskDto taskDto, Long id);

    void deleteTask(Long id);
}
