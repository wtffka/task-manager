package hexlet.code.app.service;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.User;

public interface UserService {
    User createNewUser(UserDto userDto);
    User updateUser(UserDto userDto, Long id);
    void deleteUser(Long id);

    String getCurrentUserName();

    User getCurrentUser();
}
