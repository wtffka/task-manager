package hexlet.code.service.impls;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
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
