package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAllUsers() {
        return new LinkedList<>(jdbcTemplate.query("SELECT * FROM users;", userMapper));
    }

    @Override
    public User getUserById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users " +
                    "WHERE user_id = " + id + ";", userMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotPresentException("There's no user with " + id + " id!");
        }
    }

    public List<User> getAllUserFriends(Integer userId) {
        getUserById(userId);
        return new LinkedList<>(jdbcTemplate.query("SELECT * " +
                "FROM users " +
                "WHERE users.user_id IN " +
                "(SELECT friend_id FROM friendships " +
                "WHERE friendships.user_id = " + userId + ") " +
                "ORDER BY users.user_id;", userMapper));
    }

    @Override
    public List<User> getUserCommonFriends(Integer userId, Integer otherId) {
        getUserById(userId);
        getUserById(otherId);
        return new LinkedList<>(jdbcTemplate.query("SELECT * " +
                "FROM users WHERE user_id IN(" +
                "SELECT friend_id " +
                "FROM friendships " +
                "WHERE friendships.user_id = " + userId + " " +
                "INTERSECT " +
                "SELECT friend_id " +
                "FROM friendships " +
                "WHERE friendships.user_id = " + otherId + ") " +
                "AND users.user_id NOT IN (" + userId + ", " + otherId + ");", userMapper));
    }

    @Override
    public User postUser(User user) {
        userNameCheck(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", user.getName());
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        parameters.put("birthday", user.getBirthday());
        int userId = (int) simpleJdbcInsert.executeAndReturnKey(parameters);
        user.setId(userId);
        return user;
    }

    @Override
    public User putUser(User user) {
        getUserById(user.getId());
        userNameCheck(user);
        jdbcTemplate.execute("UPDATE users SET " +
                "name = '" + user.getName() + "', " +
                "email = '" + user.getEmail() + "', " +
                "login = '" + user.getLogin() + "', " +
                "birthday = '" + user.getBirthday() + "' " +
                "WHERE user_id = " + user.getId());
        return user;
    }

    @Override
    public void putNewFriend(Integer userId, Integer friendId) {
        getUserById(userId);
        getUserById(friendId);
        jdbcTemplate.execute("INSERT INTO friendships " +
                "VALUES " +
                "(" + userId + ", " + friendId + ");");
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        getUserById(userId);
        getUserById(friendId);
        jdbcTemplate.execute("DELETE FROM friendships " +
                "WHERE user_id = " + userId + " " +
                "AND friend_id = " + friendId);
    }

    private void userNameCheck(User user) {
        if (null == user.getName() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
