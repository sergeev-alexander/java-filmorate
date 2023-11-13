package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(Integer id);

    User postUser(User user);

    User putUser(User user);

    void putNewFriend(Integer id, Integer friendId);

    void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId);

    void presenceCheck(Integer id);

}
