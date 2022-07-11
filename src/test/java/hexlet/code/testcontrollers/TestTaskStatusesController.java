package hexlet.code.testcontrollers;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.Utils;
import hexlet.code.utils.AppConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static hexlet.code.utils.Utils.toJson;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class TestTaskStatusesController {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

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
                MockMvcRequestBuilders.get(AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER
                        + AppConstants.ID, expectedTaskStatus.getId()), AppConstants.TEST_USERNAME)
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
                MockMvcRequestBuilders.get(AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER),
                        AppConstants.TEST_USERNAME)
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
        Assertions.assertThat(taskStatusRepository.findAll()
                .get(0).getName()).isEqualTo(TASK_STATUS_DTO_TEST.getName());
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

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER + AppConstants.ID,
                        expectedTaskStatus.getId())
                        .content(toJson(TASK_STATUS_DTO_UPDATE_TEST)).contentType(MediaType.APPLICATION_JSON);

        TaskStatus updatedStatus = taskStatusRepository.findAll().get(0);

        utils.perform(request, AppConstants.TEST_USERNAME)
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

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER + AppConstants.ID,
                expectedTaskStatus.getId())
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

        utils.perform(MockMvcRequestBuilders
                .delete(AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER + AppConstants.ID,
                expectedTaskStatus.getId()), AppConstants.TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(taskStatusRepository.count()).isEqualTo(0);
    }
}
