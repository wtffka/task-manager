package hexlet.code.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class AppConstants {

    // BASE_URLS FOR CONTROLLERS
    public static final String BASE_URL_FOR_USER_CONTROLLER = "/api/users";
    public static final String BASE_URL_FOR_TASK_STATUSES_CONTROLLER = "/api/statuses";
    public static final String BASE_URL_FOR_TASK_CONTROLLER = "/api/tasks";
    public static final String BASE_URL_FOR_USER_AUTH = "/api/login";
    public static final String BASE_URL_FOR_LABEL_CONTROLLER = "/api/labels";
    public static final String LOGIN = "/login";
    public static final String ID = "/{id}";
    // -----------------------------------
    // TEST CREDENTIALS
    public static final String TEST_USERNAME = "email1337@mail.email";
    public static final String TEST_PASSWORD = "password";
    //--------------------------------------------------------
    //OTHER CONSTANTS
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final List<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));
    public static final String BEARER = "Bearer";
    public static final String DELETE_TASK_SUCCESSFUL = "Task deleted successfully";
    public static final String DELETE_TASK_UNSUCCESSFUL = "The task can be deleted only by a creator.";
}
