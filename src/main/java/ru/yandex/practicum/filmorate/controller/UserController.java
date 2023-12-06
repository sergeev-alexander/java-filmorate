package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("users/{userId}/friends")
    public List<User> getAllUserFriends(@PathVariable Integer userId) {
        return userService.getAllUserFriends(userId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> getUserCommonFriends(@PathVariable Integer userId, @PathVariable Integer otherId) {
        return userService.getUserCommonFriends(userId, otherId);
    }

    @PostMapping("/users")
    public User postUser(@Valid @RequestBody User user) {
        return userService.postUser(user);
    }

    @PutMapping("/users")
    public User putUser(@Valid @RequestBody User user) {
        return userService.putUser(user);
    }

    @PutMapping("/users/{userId}/friends/{friendId}")
    public void putNewFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        userService.putNewFriend(userId, friendId);
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        userService.deleteFriend(userId, friendId);
    }

}
