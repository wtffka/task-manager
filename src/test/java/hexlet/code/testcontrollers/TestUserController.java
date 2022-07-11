package hexlet.code.testcontrollers;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hexlet.code.utils.Utils.fromJson;
import static hexlet.code.utils.AppConstants.BASE_URL_FOR_USER_CONTROLLER;
import static hexlet.code.utils.AppConstants.ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class TestUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Utils utils;

    @Test
    public void registrationUserTestPositive() throws Exception {
        assertThat(userRepository.count()).isEqualTo(0);
        utils.regDefaultUser().andExpect(status().isOk());
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void getUserPositiveTest() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                get(BASE_URL_FOR_USER_CONTROLLER + ID, expectedUser.getId()),
                expectedUser.getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {

        });

        assertThat(user.getId()).isEqualTo(expectedUser.getId());
        assertThat(user.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(user.getFirstName()).isEqualTo(expectedUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(expectedUser.getLastName());
    }

    @Test
    public void getUserNegativeTest() throws Exception {
        utils.regDefaultUser();
        final MockHttpServletResponse response = utils.perform(
                get(BASE_URL_FOR_USER_CONTROLLER))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {

        });

        assertThat(users.size()).isEqualTo(1);

    }

    @Test
    public void doRegistrationUserIncorrect() throws Exception {
        assertThat(userRepository.count()).isEqualTo(0);
        utils.regIncorrectUser().andExpect(status().isUnprocessableEntity());
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    public void getAuthenticatedPositive() throws Exception {
        utils.regDefaultUser().andExpect(status().isOk());
        utils.authDefaultUser().andExpect(status().isOk());
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void getAuthenticatedNegative() throws Exception {
        utils.regDefaultUser().andExpect(status().isOk());
        utils.authIncorrectUser().andExpect(status().isUnauthorized());
    }
}
