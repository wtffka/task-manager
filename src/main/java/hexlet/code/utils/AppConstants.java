package hexlet.code.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class AppConstants {

    // -----------------------------------
    // TEST CREDENTIALS
    public static final String TEST_USERNAME = "email1337@mail.email";
    public static final String TEST_PASSWORD = "password";
    //--------------------------------------------------------
    //OTHER CONSTANTS
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final List<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));
    public static final String BEARER = "Bearer";
}
