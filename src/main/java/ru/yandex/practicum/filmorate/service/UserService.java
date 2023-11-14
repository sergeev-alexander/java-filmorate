package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.LinkedList;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUserFriends(Integer id) {
        User user = userStorage.getUserById(id);
        List<User> friendsList = new LinkedList<>();
        for (Integer friendId : user.getFriends()) {
            friendsList.add(userStorage.getUserById(friendId));
        }
        return friendsList;
    }

    public List<User> getUsersCommonFriends(Integer id, Integer otherId) {
        getUserById(id);
        getUserById(otherId);
        List<User> commonFriends = new LinkedList<>();
        for (User friend : getAllUserFriends(id)) {
            if (getAllUserFriends(otherId).contains(friend)) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }

    public User postUser(User user) {
        return userStorage.postUser(user);
    }

    public User putUser(User user) {
        return userStorage.putUser(user);
    }

    public void putNewFriend(Integer id, Integer friendId) {
        userStorage.putNewFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userStorage.deleteFriend(id, friendId);
    }

}
