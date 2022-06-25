package hexlet.code.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class AppConstants {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final List<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));
    public static final String BASE_URL_FOR_USER_CONTROLLER = "/api/users";
    public static final String BASE_URL_FOR_USER_AUTH = "/api/login";
    public static final String ID = "/{id}";
    public static final String LOGIN = "/login";

    public static final String TEST_USERNAME = "email1337@mail.email";
    public static final String TEST_PASSWORD = "password";
    public static final String BEARER = "Bearer";
}
