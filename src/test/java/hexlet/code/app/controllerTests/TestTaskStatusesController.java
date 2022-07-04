package hexlet.code.app.controllerTests;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.testUtils.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static hexlet.code.app.testUtils.Utils.toJson;
import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.ID;
import static hexlet.code.app.utils.AppConstants.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class TestTaskStatusesController {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Utils utils;

    private static final TaskStatusDto TASK_STATUS_DTO_TEST = new TaskStatusDto("New");
    private static final TaskStatusDto TASK_STATUS_DTO_TEST_2 = new TaskStatusDto("One More New");
    private static final TaskStatusDto TASK_STATUS_DTO_UPDATE_TEST = new TaskStatusDto("Not new");

    @Test
    public void getTaskStatus() throws Exception {
        assertThat(taskStatusRepository.count()).isEqualTo(0);
        utils.regDefaultUser();
        utils.regTaskStatus(TASK_STATUS_DTO_TEST);
        assertThat(taskStatusRepository.count()).isEqualTo(1);
        TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        utils.perform(
                get(BASE_URL_FOR_TASK_STATUSES_CONTROLLER + ID, expectedTaskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(expectedTaskStatus.getName()).isEqualTo(TASK_STATUS_DTO_TEST.getName());
    }

    @Test
    public void getTaskStatuses() throws Exception {
        assertThat(taskStatusRepository.count()).isEqualTo(0);
        utils.regDefaultUser();
        utils.regTaskStatus(TASK_STATUS_DTO_TEST);
        utils.regTaskStatus(TASK_STATUS_DTO_TEST_2);
        assertThat(taskStatusRepository.count()).isEqualTo(2);

        TaskStatus expectedStatus1 = taskStatusRepository.findAll().get(0);
        TaskStatus expectedStatus2 = taskStatusRepository.findAll().get(1);

        utils.perform(
                get(BASE_URL_FOR_TASK_STATUSES_CONTROLLER), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(expectedStatus1.getName()).isEqualTo(TASK_STATUS_DTO_TEST.getName());
        assertThat(expectedStatus2.getName()).isEqualTo(TASK_STATUS_DTO_TEST_2.getName());
    }

    @Test
    public void registrationTaskStatusPositive() throws Exception {
        assertThat(taskStatusRepository.count()).isEqualTo(0);
        utils.regDefaultUser();
        utils.regTaskStatus(TASK_STATUS_DTO_TEST);
        assertThat(taskStatusRepository.count()).isEqualTo(1);
        assertThat(taskStatusRepository.findAll().get(0).getName()).isEqualTo(TASK_STATUS_DTO_TEST.getName());
    }

    @Test
    public void registrationTaskStatusNegative() throws Exception {
        assertThat(taskStatusRepository.count()).isEqualTo(0);
        utils.regDefaultUser();
        utils.regTaskStatusIncorrect(TASK_STATUS_DTO_TEST);
        assertThat(taskStatusRepository.count()).isEqualTo(0);
    }

    @Test
    public void updateTaskStatusPositive() throws Exception {
        assertThat(taskStatusRepository.count()).isEqualTo(0);
        utils.regDefaultUser();
        utils.regTaskStatus(TASK_STATUS_DTO_TEST);
        assertThat(taskStatusRepository.count()).isEqualTo(1);
        TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put(
                BASE_URL_FOR_TASK_STATUSES_CONTROLLER + ID, expectedTaskStatus.getId())
                        .content(toJson(TASK_STATUS_DTO_UPDATE_TEST)).contentType(MediaType.APPLICATION_JSON);

        TaskStatus updatedStatus = taskStatusRepository.findAll().get(0);

        utils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(updatedStatus.getName()).isEqualTo(TASK_STATUS_DTO_UPDATE_TEST.getName());
    }

    @Test
    public void updateTaskStatusNegative() throws Exception {
        assertThat(taskStatusRepository.count()).isEqualTo(0);
        utils.regDefaultUser();
        utils.regTaskStatus(TASK_STATUS_DTO_TEST);
        assertThat(taskStatusRepository.count()).isEqualTo(1);
        TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put(
                BASE_URL_FOR_TASK_STATUSES_CONTROLLER + ID, expectedTaskStatus.getId())
                .content(toJson(TASK_STATUS_DTO_UPDATE_TEST)).contentType(MediaType.APPLICATION_JSON);

        utils.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    public void deleteTaskStatusTest() throws Exception {
        assertThat(taskStatusRepository.count()).isEqualTo(0);
        utils.regDefaultUser();
        utils.regTaskStatus(TASK_STATUS_DTO_TEST);
        assertThat(taskStatusRepository.count()).isEqualTo(1);

        TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        utils.perform(
                delete(BASE_URL_FOR_TASK_STATUSES_CONTROLLER + ID,
                expectedTaskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(taskStatusRepository.count()).isEqualTo(0);
    }
}
