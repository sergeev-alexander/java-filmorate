package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer idCounter = 1;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @PostMapping("/users")
    public User postUser(@Valid @RequestBody User user) {
        user.setId(idCounter++);
        userNameCheck(user);
        userMap.put(user.getId(), user);
        log.info("Added new user: {}", user);
        return user;
    }

    @PutMapping("/users")
    public User putUser(@Valid @RequestBody User user) {
        if (user.getId() == null || !userMap.containsKey(user.getId())) {
            log.error("User validation error : There's no user with {} id!", user.getId());
            throw new ItemNotPresentException("There's no user with " + user.getId() + " id!");
        }
        userNameCheck(user);
        userMap.put(user.getId(), user);
        log.info("Updated user: {}", user);
        return user;
    }

    private void userNameCheck(User user) {
        if (null == user.getName() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}

