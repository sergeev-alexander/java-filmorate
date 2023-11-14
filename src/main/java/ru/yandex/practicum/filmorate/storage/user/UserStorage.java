package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(Integer id);

    User postUser(User user);

    User putUser(User user);

    void putNewFriend(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

}
