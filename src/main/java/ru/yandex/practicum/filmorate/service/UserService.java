package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public List<User> getAllUserFriends(Integer userId) {
        return userStorage.getAllUserFriends(userId);
    }

    public List<User> getUserCommonFriends(Integer userId, Integer otherId) {
        return userStorage.getUserCommonFriends(userId, otherId);
    }

    public User postUser(User user) {
        return userStorage.postUser(user);
    }

    public User putUser(User user) {
        return userStorage.putUser(user);
    }

    public void putNewFriend(Integer userId, Integer friendId) {
        userStorage.putNewFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

}
