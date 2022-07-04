package hexlet.code.app.service.impls;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus createTaskStatus(TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        setTaskStatusData(taskStatusDto, taskStatus);
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(TaskStatusDto taskStatusDto, Long id) {
        final TaskStatus taskStatusToUpdate = taskStatusRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        setTaskStatusData(taskStatusDto, taskStatusToUpdate);
        return taskStatusRepository.save(taskStatusToUpdate);
    }

    @Override
    public void deleteTaskStatus(Long id) {
        taskStatusRepository.delete(taskStatusRepository.findById(id).get());
    }

    private void setTaskStatusData(TaskStatusDto taskStatusDto, TaskStatus taskStatus) {
        taskStatus.setName(taskStatusDto.getName());
    }
}
