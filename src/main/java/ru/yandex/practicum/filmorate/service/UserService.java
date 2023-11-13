package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUserFriends(Integer id) {
        User user = userStorage.getUserById(id);
        ArrayList<User> friendsList = new ArrayList<>();
        if (user.getFriends() == null) {
            return friendsList;
        }
        for (Integer friendId : user.getFriends()) {
            friendsList.add(userStorage.getUserById(friendId));
        }
        return friendsList;
    }

    public List<User> getUsersCommonFriends(Integer id, Integer otherId) {
        userStorage.presenceCheck(id);
        userStorage.presenceCheck(otherId);
        List<User> commonFriends = new ArrayList<>();
        for(User friend : getAllUserFriends(id)) {
            if (getAllUserFriends(otherId).contains(friend)) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }

}
