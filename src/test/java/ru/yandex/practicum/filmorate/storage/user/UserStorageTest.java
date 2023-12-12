package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class UserStorageTest {

    private final JdbcTemplate jdbcTemplate;
    UserDbStorage userDbStorage;

    @Autowired
    UserStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate, new UserMapper());
        jdbcTemplate.execute("INSERT INTO users (name, email, login, birthday) " +
                "VALUES " +
                "('name_one', 'email@one.com', 'login_one', '2001-01-01'), " +
                "('name_two', 'email@two.com', 'login_two', '2002-02-02'), " +
                "('name_three', 'email@three.com', 'login_three', '2003-03-03');");
    }

    @AfterEach
    void clearDb() {
        jdbcTemplate.execute("DELETE FROM users; " +
                "ALTER TABLE users " +
                "ALTER COLUMN user_id " +
                "RESTART WITH 1;");
    }

    @Test
    void getAllUsers_normal_behavior() {
        List<User> receivedUserList = userDbStorage.getAllUsers();
        assertEquals(receivedUserList.size(), 3, "Received list size is not 3!");
        assertEquals(receivedUserList.get(0), createSomeUsers().get(0),
                "Received user is not equal to testing user!");
        assertEquals(receivedUserList.get(1), createSomeUsers().get(1),
                "Received user is not equal to testing user!");
        assertEquals(receivedUserList.get(2), createSomeUsers().get(2),
                "Received user is not equal to testing user!");
    }

    @Test
    void getAllUsers_no_users_return_empty_list() {
        clearDb();
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate, new UserMapper());
        List<User> receivedUserList = userDbStorage.getAllUsers();
        assertNotNull(receivedUserList,
                "Received list is null!");
        assertEquals(0, receivedUserList.size(),
                "Received list size is not 0!");
    }

    @Test
    void getUserById_normal_behavior() {
        assertEquals(createSomeUsers().get(0), userDbStorage.getUserById(1),
                "Received user is not equal to testing user!");
        assertEquals(createSomeUsers().get(1), userDbStorage.getUserById(2),
                "Received user is not equal to testing user!");
        assertEquals(createSomeUsers().get(2), userDbStorage.getUserById(3),
                "Received user is not equal to testing user!");
    }

    @Test
    void getUserById_wrong_id_throws_itemNotPresentException() {
        try {
            userDbStorage.getUserById(123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no user with 123 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    @Test
    void getAllUsersFriends_normal_behavior() {
        jdbcTemplate.execute("INSERT INTO friendships (user_id, friend_id) " +
                "VALUES " +
                "(1, 2), " +
                "(1, 3);");
        assertEquals(2, userDbStorage.getAllUserFriends(1).size(),
                "Received friends list size is wrong!");
        assertEquals(List.of(createSomeUsers().get(1), createSomeUsers().get(2)),
                userDbStorage.getAllUserFriends(1),
                "Received friends are wrong!");
    }

    @Test
    void getAllUsersFriends_no_friends_returns_empty_list() {
        assertNotNull(userDbStorage.getAllUserFriends(1),
                "Received list is null!");
        assertTrue(userDbStorage.getAllUserFriends(1).isEmpty(),
                "Received list is not empty!");
    }

    @Test
    void getUserCommonFriends_normal_behavior() {
        jdbcTemplate.execute("INSERT INTO friendships (user_id, friend_id) " +
                "VALUES " +
                "(1, 3), " +
                "(2, 3);");
        assertEquals(1, userDbStorage.getUserCommonFriends(1, 2).size(),
                "Received friends list size is wrong!");
        assertEquals(List.of(createSomeUsers().get(2)),
                userDbStorage.getUserCommonFriends(1, 2),
                "Received friends are wrong!");
    }

    @Test
    void getUserCommonFriends_no_common_friends_returns_empty_list() {
        assertNotNull(userDbStorage.getUserCommonFriends(1, 2),
                "Received list is null!");
        assertTrue(userDbStorage.getUserCommonFriends(1, 2).isEmpty(),
                "Received list is not empty!");
    }

    @Test
    void postUser_normal_behavior() {
        clearDb();
        User user = createSomeUsers().get(0);
        user.setId(null);
        userDbStorage.postUser(user);
        assertNotNull(userDbStorage.getUserById(1),
                "Received user is null!");
        assertEquals(userDbStorage.getUserById(1), createSomeUsers().get(0),
                "Received wrong user!");
    }

    @Test
    void postUser_with_id_must_overwrite_id() {
        clearDb();
        assertEquals(1, userDbStorage.postUser(createSomeUsers().get(2)).getId(),
                "Id was not overwritten!");
    }

    @Test
    void putUser_normal_behavior() {
        User user = createSomeUsers().get(0);
        user.setName("updated_name");
        user.setEmail("updated@email.com");
        user.setLogin("updated_login");
        user.setBirthday(LocalDate.of(2004, 4, 4));
        assertEquals(userDbStorage.putUser(user), userDbStorage.getUserById(1),
                "User was not / wrong updated!");
    }

    @Test
    void putUser_not_existing_user_throws_itemNotPresentException() {
        User user = createSomeUsers().get(0);
        user.setId(123);
        try {
            userDbStorage.putUser(user);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no user with 123 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    @Test
    void putNewFriend_normal_behavior() {
        userDbStorage.putNewFriend(1, 2);
        assertEquals(createSomeUsers().get(1), userDbStorage.getAllUserFriends(1).get(0),
                "Received wrong friend!");
    }

    @Test
    void putNewFriend_wrong_friendId_throws_itemNotPresentException() {
        try {
            userDbStorage.putNewFriend(1, 123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no user with 123 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    @Test
    void deleteFriend_normal_behavior() {
        jdbcTemplate.execute("INSERT INTO friendships " +
                "VALUES " +
                "(1, 2), " +
                "(1, 3);");
        userDbStorage.deleteFriend(1, 2);
        assertEquals(1, userDbStorage.getAllUserFriends(1).size(),
                "Friend was not deleted!");
        assertEquals(createSomeUsers().get(2), userDbStorage.getAllUserFriends(1).get(0),
                "Wrong friend was deleted!");
    }

    @Test
    void deleteFriend_wrong_friendId_throws_itemNotPresentException() {
        try {
            userDbStorage.deleteFriend(1, 123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no user with 123 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    private List<User> createSomeUsers() {

        User userOne = new User();
        userOne.setId(1);
        userOne.setName("name_one");
        userOne.setEmail("email@one.com");
        userOne.setLogin("login_one");
        userOne.setBirthday(LocalDate.of(2001, 1, 1));

        User userTwo = new User();
        userTwo.setId(2);
        userTwo.setName("name_two");
        userTwo.setEmail("email@two.com");
        userTwo.setLogin("login_two");
        userTwo.setBirthday(LocalDate.of(2002, 2, 2));

        User userThree = new User();
        userThree.setId(3);
        userThree.setName("name_three");
        userThree.setEmail("email@three.com");
        userThree.setLogin("login_three");
        userThree.setBirthday(LocalDate.of(2003, 3, 3));

        return List.of(userOne, userTwo, userThree);
    }

}
