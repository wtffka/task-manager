package hexlet.code.app.testUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.app.dto.LoginDto;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.utils.JWTHelper;
import hexlet.code.app.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.app.utils.AppConstants.TEST_USERNAME;
import static hexlet.code.app.utils.AppConstants.TEST_PASSWORD;
import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_TASK_STATUSES_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_TASK_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_USER_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_USER_AUTH;
import static hexlet.code.app.utils.AppConstants.MAPPER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@Component
public class Utils {

    private final UserDto testRegistrationUserDto = new UserDto(
            TEST_USERNAME,
            "name",
            "surname",
            TEST_PASSWORD
    );

    private final UserDto testRegistrationDtoEmpty = new UserDto();

    private final LoginDto testAuthenticationDto = new LoginDto(
            TEST_USERNAME,
            TEST_PASSWORD
    );

    private final LoginDto testAuthenticationIncorrectDto = new LoginDto();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTHelper jwtHelper;

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationUserDto);
    }

    public ResultActions regIncorrectUser() throws Exception {
        return regUser(testRegistrationDtoEmpty);
    }

    public ResultActions authDefaultUser() throws Exception {
        return authUser(testAuthenticationDto);
    }

    public ResultActions authIncorrectUser() throws Exception {
        return authUser(testAuthenticationIncorrectDto);
    }

    public ResultActions regUser(final UserDto userDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(BASE_URL_FOR_USER_CONTROLLER)
                .content(toJson(userDto))
                .contentType(MediaType.APPLICATION_JSON);
        return perform(request);
    }

    public ResultActions regTaskStatus(final TaskStatusDto taskStatusDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(BASE_URL_FOR_TASK_STATUSES_CONTROLLER)
                .content(toJson(taskStatusDto))
                .contentType(MediaType.APPLICATION_JSON);
        return perform(request, TEST_USERNAME);
    }
    public ResultActions regTaskStatusIncorrect(final TaskStatusDto taskStatusDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(BASE_URL_FOR_TASK_STATUSES_CONTROLLER)
                .content(toJson(taskStatusDto))
                .contentType(MediaType.APPLICATION_JSON);
        return perform(request);
    }

    public ResultActions regTask(final TaskDto taskDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(BASE_URL_FOR_TASK_CONTROLLER)
                .content(toJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);
        return perform(request, TEST_USERNAME);
    }

    public ResultActions regTaskIncorrect(final TaskDto taskDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(BASE_URL_FOR_TASK_CONTROLLER)
                .content(toJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);
        return perform(request);
    }

    public ResultActions authUser(final LoginDto loginDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(BASE_URL_FOR_USER_AUTH)
                .content(toJson(loginDto))
                .contentType(MediaType.APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String username) throws Exception {
        final String token = jwtHelper.expiring(Map.of("email", username));
        request.header(AUTHORIZATION, token);
        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static String toJson(Object o) throws JsonProcessingException {
        return MAPPER.findAndRegisterModules().writeValueAsString(o);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> type) throws JsonProcessingException {
        return MAPPER.findAndRegisterModules().readValue(json, type);
    }

}
