package hexlet.code.app.controllers;

import com.rollbar.notifier.Rollbar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @Autowired
    Rollbar rollbar;

    @Value("${rollbar_token:}")
    private String rollbarToken;

    @GetMapping("/welcome")
    public String rootWelcome() {

        rollbar.debug("Here is some debug message");
        return rollbarToken;
    }
}
