package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;

public interface TaskStatusService {

    TaskStatus createTaskStatus(TaskStatusDto taskStatusDto);

    TaskStatus updateTaskStatus(TaskStatusDto taskStatusDto, Long id);

    void deleteTaskStatus(Long id);

}
