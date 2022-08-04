package hexlet.code.testcontrollers;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;

import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.AppConstants;
import hexlet.code.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hexlet.code.utils.Utils.fromJson;
import static hexlet.code.utils.Utils.toJson;
import static hexlet.code.utils.AppConstants.TEST_USERNAME;
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

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final Utils utils;

    @Autowired
    public TestTaskController(UserRepository userRepository,
                              TaskRepository taskRepository,
                              TaskStatusRepository taskStatusRepository,
                              Utils utils) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.utils = utils;
    }

    private static final TaskStatusDto TEST_TASK_STATUS_DTO = new TaskStatusDto("New");

    @BeforeEach
    void init() throws Exception {
        utils.regDefaultUser();
        regTaskStatus(TEST_TASK_STATUS_DTO);
    }

    @Test
    public void getTask() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        regTask(new TaskDto(
                "name",
                "desc",
                executorId,
                taskStatusId,
                null
        ));

        Task expectedTask = taskRepository.findAll().get(0);

        utils.perform(
                        get("/api/tasks/{id}", expectedTask.getId()),
                        expectedTask.getAuthor().getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(expectedTask.getName()).isEqualTo("name");
        assertThat(expectedTask.getTaskStatus().getId()).isEqualTo(taskStatusId);
        assertThat(expectedTask.getDescription()).isEqualTo("desc");
        assertThat(expectedTask.getExecutor().getId()).isEqualTo(executorId);
    }

    @Test
    public void getAllTasksQueryDsl() throws Exception {
        regTaskStatus(new TaskStatusDto("not new"));

        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final Long taskStatusId2 = taskStatusRepository.findAll().get(1).getId();

        regTask(new TaskDto(
                "name",
                "desc",
                executorId,
                taskStatusId,
                null
        ));

        regTask(new TaskDto(
                "name2",
                "desc2",
                executorId,
                taskStatusId2,
                null
        ));

        String queryDsl = "?taskStatusId=" + taskStatusId + "&executorId=" + executorId;
        final MockHttpServletResponse response = utils.perform(
                        get("/api/tasks" + queryDsl), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("name");
        assertThat(response.getContentAsString()).contains("desc");
        assertThat(response.getContentAsString()).doesNotContain("name2");
        assertThat(response.getContentAsString()).doesNotContain("desc2");
        List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks.size()).isEqualTo(1);
    }

    @Test
    public void getTasks() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));

        regTask(new TaskDto(
                "nameTwo",
                "descTwo",
                executorId,
                taskStatusId,
                null
        ));

        assertThat(taskRepository.count()).isEqualTo(2);

        Task expectedTask1 = taskRepository.findAll().get(0);
        Task expectedTask2 = taskRepository.findAll().get(1);


        utils.perform(get("/api/tasks"), expectedTask1.getAuthor().getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(expectedTask1.getName()).isEqualTo("nameOne");
        assertThat(expectedTask2.getName()).isEqualTo("nameTwo");
        assertThat(expectedTask1.getTaskStatus().getId()).isEqualTo(taskStatusId);
        assertThat(expectedTask2.getTaskStatus().getId()).isEqualTo(taskStatusId);
        assertThat(expectedTask1.getDescription()).isEqualTo("descOne");
        assertThat(expectedTask2.getDescription()).isEqualTo("descTwo");
        assertThat(expectedTask1.getExecutor().getId()).isEqualTo(executorId);
        assertThat(expectedTask2.getExecutor().getId()).isEqualTo(executorId);
    }

    @Test
    public void createTaskPositive() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        assertThat(taskRepository.count()).isEqualTo(0);
        regTask(new TaskDto(
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
        regTaskIncorrect(new TaskDto(
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

        regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));

        Task expectedTask = taskRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put("/api/tasks/{id}", expectedTask.getId())
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
        assertThat(updatedTask.getDescription()).isEqualTo("descTwo");
        assertThat(updatedTask.getExecutor().getId()).isEqualTo(executorId);
    }

    @Test
    public void updateTaskNegative() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();

        regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));

        Task expectedTask = taskRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put("/api/tasks/{id}", expectedTask.getId())
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
        assertThat(updatedTask.getDescription()).isEqualTo("descOne");
        assertThat(updatedTask.getExecutor().getId()).isEqualTo(executorId);
    }

    @Test
    public void deleteTaskPositive() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        assertThat(taskRepository.count()).isEqualTo(0);
        regTask(new TaskDto(
                "nameOne",
                "descOne",
                executorId,
                taskStatusId,
                null
        ));

        assertThat(taskRepository.count()).isEqualTo(1);

        Task expectedTask = taskRepository.findAll().get(0);

        utils.perform(delete("/api/tasks/{id}",
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

        regTask(new TaskDto(
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

        utils.perform(delete("/api/tasks/{id}",
                        expectedTask.getId()), userRepository.findByEmail("a@b.ru").get().getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(taskRepository.count()).isEqualTo(1);

    }

    private ResultActions regTask(final TaskDto taskDto) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/tasks")
                .content(toJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        return utils.perform(request, AppConstants.TEST_USERNAME);
    }

    private ResultActions regTaskIncorrect(final TaskDto taskDto) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/tasks")
                .content(toJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        return utils.perform(request);
    }

    private ResultActions regTaskStatus(final TaskStatusDto taskStatusDto) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/statuses")
                .content(toJson(taskStatusDto))
                .contentType(MediaType.APPLICATION_JSON);

        return utils.perform(request, AppConstants.TEST_USERNAME);

    }
}
