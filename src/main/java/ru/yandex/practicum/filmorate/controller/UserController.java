package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    UserStorage userStorage;
    UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userStorage.getUserById(id);
    }

    @GetMapping("users/{id}/friends")
    public List<User> getAllUserFriends(@PathVariable Integer id) {
        return userService.getAllUserFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getUsersCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getUsersCommonFriends(id, otherId);
    }

    @PostMapping("/users")
    public User postUser(@Valid @RequestBody User user) {
        return userStorage.postUser(user);
    }

    @PutMapping("/users")
    public User putUser(@Valid @RequestBody User user) {
        return userStorage.putUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void putNewFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userStorage.putNewFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userStorage.deleteFriend(id, friendId);
    }

}
