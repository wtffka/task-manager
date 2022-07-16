package hexlet.code.controllers;

import com.rollbar.notifier.Rollbar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    private final Rollbar rollbar;

    @Autowired
    public WelcomeController(Rollbar rollbar) {
        this.rollbar = rollbar;
    }

    @GetMapping
    public String rootWelcome() {

        rollbar.debug("Here is some debug message");
        return "Welcome to Spring!";
    }
}
