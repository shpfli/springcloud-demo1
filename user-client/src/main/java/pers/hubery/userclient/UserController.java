package pers.hubery.userclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserClient userClient;

    @PostMapping
    public User createUser(@RequestBody User user) {

        LOGGER.info("call user-service to create user: {}", user);

        User result = userClient.createUser(user);

        LOGGER.info("Received result from user-service. result: {}", result);

        return result;
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        LOGGER.info("call user-service to updateUser. id: {}, User: {}", id, user);
        user.setId(id);
        User result = userClient.updateUser(user.getId(), user);

        LOGGER.info("Received result from user-service. result: {}", result);
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        LOGGER.info("call user-service to deleteUser. id: {}", id);
        userClient.deleteUser(id);
        LOGGER.info("Received result from user-service. result: {}", "success");
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        LOGGER.info("call user-service to getUser. id: {}", id);
        User user = userClient.getUser(id);
        LOGGER.info("Received result from user-service. result: {}", user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {

        LOGGER.info("call user-service to getUsers.");
        List<User> users = userClient.getUsers();
        LOGGER.info("Received result from user-service. result: {}", users);
        return users;
    }
}

