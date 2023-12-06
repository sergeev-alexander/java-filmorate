package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Service
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer idCounter = 1;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUserById(Integer id) {
        return Optional.ofNullable(userMap.get(id)).orElseThrow(
                () -> new ItemNotPresentException("There's no user with " + id + " id!"));
    }

    @Override
    public List<User> getAllUserFriends(Integer id) {
        return null;
    }

    @Override
    public List<User> getUserCommonFriends(Integer id, Integer otherId) {
        return null;
    }

    @Override
    public User postUser(User user) {
        user.setId(idCounter++);
        userNameCheck(user);
        userMap.put(user.getId(), user);
        log.info("Added new user: {}", user);
        return user;
    }

    @Override
    public User putUser(User user) {
        if (user.getId() == null) {
            log.error("User validation error : User has an empty id!");
            throw new ItemNotPresentException("User has an empty id!");
        }
        getUserById(user.getId());
        userNameCheck(user);
        userMap.put(user.getId(), user);
        log.info("Updated user: {}", user);
        return user;
    }

    @Override
    public void putNewFriend(Integer id, Integer friendId) {

    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {

    }

    private void userNameCheck(User user) {
        if (null == user.getName() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
