package pl.pollub.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pollub.domain.User;
import pl.pollub.exception.UserNotFoundException;
import pl.pollub.exception.UserUsernameExistException;
import pl.pollub.repository.InMemoryUserRepository;
import pl.pollub.service.UserService;

import java.util.List;
import java.util.Optional;

/**
 * Created by konrad on 29.07.17.
 */
@Service
public class UserServiceImpl implements UserService{

    private InMemoryUserRepository userRepository;

    @Autowired
    public UserServiceImpl(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        if (ifUserWithUsernameExist(user.getUsername()))
            throw new UserUsernameExistException(user.getUsername());
        return userRepository.save(user);
    }

    private boolean ifUserWithUsernameExist(String username) {
        return userRepository.findUserByUsername(username) != null;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        User user = Optional.ofNullable(userRepository.findOne(id)).orElseThrow(() -> new UserNotFoundException(id));
        return user;
    }

    @Override
    public void deleteUserById(Long id) {
        Optional.ofNullable(userRepository.findOne(id)).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(id);
    }

    @Override
    public void deleteUser(User user) {
        Long userId = user.getId();
        Optional.ofNullable(userRepository.findOne(userId)).orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }

    @Override
    public User getUserByUsername(String username) {
        User user = Optional.ofNullable(userRepository.findUserByUsername(username)).orElseThrow(() -> new UserNotFoundException());
        return user;
    }
}
