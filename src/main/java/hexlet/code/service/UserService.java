package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

public interface UserService {
    User createNewUser(UserDto userDto);
    User updateUser(UserDto userDto, Long id);
    void deleteUser(Long id);

    String getCurrentEmail();

    User getCurrentUser();
}
