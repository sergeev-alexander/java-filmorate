package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(Integer id);

    List<User> getAllUserFriends(Integer id);

    List<User> getUserCommonFriends(Integer userId, Integer otherId);

    User postUser(User user);

    User putUser(User user);

    void putNewFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

}
