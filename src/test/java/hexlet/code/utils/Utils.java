package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class Utils {

    private final UserDto testRegistrationUserDto = new UserDto(
            AppConstants.TEST_USERNAME,
            "name",
            "surname",
            AppConstants.TEST_PASSWORD
    );

    private final UserDto testRegistrationDtoEmpty = new UserDto();

    private final LoginDto testAuthenticationDto = new LoginDto(
            AppConstants.TEST_USERNAME,
            AppConstants.TEST_PASSWORD
    );

    private final LoginDto testAuthenticationIncorrectDto = new LoginDto();

    private final MockMvc mockMvc;

    private final JWTHelper jwtHelper;

    @Autowired
    public Utils(MockMvc mockMvc, JWTHelper jwtHelper) {
        this.mockMvc = mockMvc;
        this.jwtHelper = jwtHelper;
    }

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
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/users")
                .content(toJson(userDto))
                .contentType(MediaType.APPLICATION_JSON);
        return perform(request);
    }

    public ResultActions authUser(final LoginDto loginDto) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/login")
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
        return AppConstants.MAPPER.findAndRegisterModules().writeValueAsString(o);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> type) throws JsonProcessingException {
        return AppConstants.MAPPER.findAndRegisterModules().readValue(json, type);
    }

}
