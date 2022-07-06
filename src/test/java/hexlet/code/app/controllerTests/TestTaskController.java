package hexlet.code.app.controllerTests;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;

import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.testUtils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static hexlet.code.app.testUtils.Utils.toJson;
import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_TASK_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.ID;
import static hexlet.code.app.utils.AppConstants.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class TestTaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Utils utils;

    private static final TaskStatusDto TEST_TASK_STATUS_DTO = new TaskStatusDto("New");

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @BeforeEach
    void init() throws Exception {
        utils.regDefaultUser();
        utils.regTaskStatus(TEST_TASK_STATUS_DTO);
    }

    @Test
    public void getTask() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        utils.regTask(new TaskDto(
                "name",
                "desc",
                executorId,
                taskStatusId,
                null
        ));

        Task expectedTask = taskRepository.findAll().get(0);

        utils.perform(
                        get(BASE_URL_FOR_TASK_CONTROLLER + ID, expectedTask.getId()),
                        expectedTask.getAuthor().getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(expectedTask.getName()).isEqualTo("name");
        assertThat(expectedTask.getTaskStatus().getId()).isEqualTo(taskStatusId);
        assertThat(expectedTask.getDesc()).isEqualTo("desc");
        assertThat(expectedTask.getExecutor().getId()).isEqualTo(executorId);
    }

    @Test
    public void getTasks() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        utils.regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));

        utils.regTask(new TaskDto(
                "nameTwo",
                "descTwo",
                executorId,
                taskStatusId,
                null
        ));

        assertThat(taskRepository.count()).isEqualTo(2);

        Task expectedTask1 = taskRepository.findAll().get(0);
        Task expectedTask2 = taskRepository.findAll().get(1);


        utils.perform(
                        get(BASE_URL_FOR_TASK_CONTROLLER), expectedTask1.getAuthor().getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(expectedTask1.getName()).isEqualTo("nameOne");
        assertThat(expectedTask2.getName()).isEqualTo("nameTwo");
        assertThat(expectedTask1.getTaskStatus().getId()).isEqualTo(taskStatusId);
        assertThat(expectedTask2.getTaskStatus().getId()).isEqualTo(taskStatusId);
        assertThat(expectedTask1.getDesc()).isEqualTo("descOne");
        assertThat(expectedTask2.getDesc()).isEqualTo("descTwo");
        assertThat(expectedTask1.getExecutor().getId()).isEqualTo(executorId);
        assertThat(expectedTask2.getExecutor().getId()).isEqualTo(executorId);
    }

    @Test
    public void createTaskPositive() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        assertThat(taskRepository.count()).isEqualTo(0);
        utils.regTask(new TaskDto(
                "name",
                "desc",
                executorId,
                taskStatusId,
                null
        ));

        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    public void createTaskNegative() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        assertThat(taskRepository.count()).isEqualTo(0);
        utils.regTaskIncorrect(new TaskDto(
                "name",
                "desc",
                executorId,
                taskStatusId,
                null
        ));
        assertThat(taskRepository.count()).isEqualTo(0);
    }

    @Test
    public void updateTaskPositive() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        utils.regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));

        Task expectedTask = taskRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put(
                BASE_URL_FOR_TASK_CONTROLLER + ID, expectedTask.getId())
                .content(toJson(new TaskDto(
                        "nameTwo",
                        "descTwo",
                        executorId,
                        taskStatusId,
                        null
                ))).contentType(MediaType.APPLICATION_JSON);

        Task updatedTask = taskRepository.findAll().get(0);

        utils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(updatedTask.getName()).isEqualTo("nameTwo");
        assertThat(updatedTask.getTaskStatus().getId()).isEqualTo(taskStatusId);
        assertThat(updatedTask.getDesc()).isEqualTo("descTwo");
        assertThat(updatedTask.getExecutor().getId()).isEqualTo(executorId);
    }

    @Test
    public void updateTaskNegative() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        utils.regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));

        Task expectedTask = taskRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put(
                BASE_URL_FOR_TASK_CONTROLLER + ID, expectedTask.getId())
                .content(toJson(new TaskDto(
                        "nameTwo",
                        "descTwo",
                        executorId,
                        taskStatusId,
                        null
                ))).contentType(MediaType.APPLICATION_JSON);

        Task updatedTask = taskRepository.findAll().get(0);

        utils.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertThat(updatedTask.getName()).isEqualTo("nameOne");
        assertThat(updatedTask.getTaskStatus().getId()).isEqualTo(taskStatusId);
        assertThat(updatedTask.getDesc()).isEqualTo("descOne");
        assertThat(updatedTask.getExecutor().getId()).isEqualTo(executorId);
    }

    @Test
    public void deleteTaskPositive() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        assertThat(taskRepository.count()).isEqualTo(0);
        utils.regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));

        assertThat(taskRepository.count()).isEqualTo(1);

        Task expectedTask = taskRepository.findAll().get(0);

        utils.perform(
                delete(BASE_URL_FOR_TASK_CONTROLLER + ID,
                 expectedTask.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(taskRepository.count()).isEqualTo(0);
    }

    @Test
    public void deleteTaskNegative() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        assertThat(taskRepository.count()).isEqualTo(0);

        utils.regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));
        utils.regUser(new UserDto(
                "a@b.ru",
                "s",
                "b",
                "12345678"
        ));
        assertThat(taskRepository.count()).isEqualTo(1);

        Task expectedTask = taskRepository.findAll().get(0);

        utils.perform(
                delete(BASE_URL_FOR_TASK_CONTROLLER + ID,
                expectedTask.getId()), userRepository.findByEmail("a@b.ru").get().getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(taskRepository.count()).isEqualTo(1);

    }
}
