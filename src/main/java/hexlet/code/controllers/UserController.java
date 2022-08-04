package hexlet.code.controllers;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserRepository userRepository;

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserRepository userRepository, UserServiceImpl userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping({"/{id}"})
    @Operation(summary = "Get User by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public User getUser(@Parameter(description = "User id to show", required = true)
                        @PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User with that ID not found"));
    }

    @GetMapping
    @Operation(summary = "Get list of users")
    @ApiResponse(responseCode = "200", description = "List of all users", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Create new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "422", description = "Data validation failed/ email already exist")
    })
    @ResponseStatus(CREATED)
    public User registerNew(@Parameter(description = "Data to create User", required = true)
                            @RequestBody @Valid UserDto userDto) {
        return userService.createNewUser(userDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "403", description = "Operation available only for owner"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "422", description = "Data validation failed")
    })
    public User updateUser(@Parameter(description = "Data to update user", required = true)
                           @RequestBody @Valid UserDto userDto,
                           @Parameter(description = "Id of User to be updated", required = true)
                           @PathVariable Long id) {
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "403", description = "Operation available only for owner"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteUser(@Parameter(description = "Id of User to be deleted", required = true)
                           @PathVariable Long id) {
        userService.deleteUser(id);
    }
}
