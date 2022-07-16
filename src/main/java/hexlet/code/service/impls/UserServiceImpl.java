package hexlet.code.service.impls;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static hexlet.code.utils.AppConstants.DEFAULT_AUTHORITIES;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createNewUser(UserDto userDto) {
        return userRepository.save(fromUserDto(userDto));
    }

    @Override
    public User updateUser(UserDto userDto, Long id) {
        final User userToUpdate = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        User updatedUser = fromUserDto(userDto);
        updatedUser.setId(userToUpdate.getId());
        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User userToDelete = userRepository.findById(id).
                orElseThrow(() -> new NoSuchElementException("User with that ID not found"));
        if (userToDelete.equals(getCurrentUser())) {
            userRepository.delete(userToDelete);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::buildSpringUser)
                .orElseThrow(() -> new UsernameNotFoundException("User with that username not found"));
    }

    private User fromUserDto(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return user;
    }

    private UserDetails buildSpringUser(final User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                DEFAULT_AUTHORITIES
        );
    }

    @Override
    public String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentEmail()).get();
    }

}
