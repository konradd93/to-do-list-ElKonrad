package pl.pollub.service;

import pl.pollub.domain.User;

import java.util.List;

/**
 * Created by konrad on 29.07.17.
 */
public interface UserService {

    User save(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUserById(Long id);

    void deleteUser(User user);

    User getUserByUsername(String username);
}
