package pers.hubery.userclient;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("user-service")
public interface UserClient {

    @PostMapping("/users")
    User createUser(User user);

    @PutMapping("/users/{id}")
    User updateUser(@PathVariable("id") Long id, @RequestBody User user);

    @DeleteMapping("/users/{id}")
    void deleteUser(Long id);

    @GetMapping("/users/{id}")
    User getUser(Long id);

    @GetMapping("/users")
    List<User> getUsers();
}
