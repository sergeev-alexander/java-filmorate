package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
public class UserController {

    private static final Map<Integer, User> userMap = new HashMap<>();
    private Integer idCounter = 1;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @PostMapping("/users")
    public User postUser(@Valid @RequestBody User user) {
        user.setId(idCounter++);
        if (null == user.getName()) {
            user.setName(user.getLogin());
        }
        userMap.put(user.getId(), user);
        log.info("Added new user: " + user);
        return user;
    }

    @PutMapping("/users")
    public User putUser(@Valid @RequestBody User user) {
        if (null == user.getName()) {
            user.setName(user.getLogin());
        }
        userMap.put(user.getId(), user);
        log.info("Updated user: " + user);
        return user;
    }

    public static boolean isUserPresent(Integer id) {
        return userMap.containsKey(id);
    }

}

