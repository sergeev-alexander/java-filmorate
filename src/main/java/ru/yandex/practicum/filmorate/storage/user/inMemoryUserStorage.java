package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class inMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer idCounter = 1;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUserById(Integer id) {
        presenceCheck(id);
        return userMap.get(id);
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
        presenceCheck(user.getId());
        userNameCheck(user);
        userMap.put(user.getId(), user);
        log.info("Updated user: {}", user);
        return user;
    }

    @Override
    public void putNewFriend(Integer id, Integer friendId) {
        presenceCheck(id);
        presenceCheck(friendId);
        userMap.get(id).getFriends().add(friendId);
        userMap.get(friendId).getFriends().add(id);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        presenceCheck(id);
        presenceCheck(friendId);
        userMap.get(id).getFriends().remove(friendId);
        userMap.get(friendId).getFriends().remove(id);
    }

    public void presenceCheck(Integer id) {
        if (id == null || !userMap.containsKey(id)) {
            log.error("User validation error : There's no user with {} id!", id);
            throw new ItemNotPresentException("There's no user with " + id + " id!");
        }
    }

    private void userNameCheck(User user) {
        if (null == user.getName() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
