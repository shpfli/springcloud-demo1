package pers.hubery.userservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AtomicLong counter = new AtomicLong();
    private final List<User> users = new ArrayList<>();

    @Override
    public User createUser(User user) {
        user.setId(counter.incrementAndGet());
        users.add(user);

        LOGGER.info("User created: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .forEach(u -> {
                    u.setName(user.getName());
                    u.setAge(user.getAge());
                });

        LOGGER.info("User updated: {}", user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        users.removeIf(user -> user.getId().equals(id));
        LOGGER.info("User deleted: {}", id);
    }

    @Override
    public User getUser(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);

    }

    @Override
    public List<User> getUsers() {
        LOGGER.info("Users retrieved: {}", users);
        return users;
    }
}

