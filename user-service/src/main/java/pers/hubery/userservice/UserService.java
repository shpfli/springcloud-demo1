package pers.hubery.userservice;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    User getUser(Long id);

    List<User> getUsers();
}

