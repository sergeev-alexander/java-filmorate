package ru.yandex.practicum.filmorate.storage.rate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
class RateStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private RateStorage rateStorage;
    private FilmStorage filmStorage;

    @Autowired
    public RateStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        rateStorage = new RateDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, new FilmMapper());
        jdbcTemplate.execute("INSERT INTO users (name, email, login, birthday) " +
                "VALUES " +
                "('name_one', 'email@one.com', 'login_one', '2001-01-01'), " +
                "('name_two', 'email@two.com', 'login_two', '2002-02-02'), " +
                "('name_three', 'email@three.com', 'login_three', '2003-03-03'); " +
                "INSERT INTO films (mpa_id, name, description, release_date, duration) " +
                "VALUES " +
                "(1, 'film_one_name', 'film_one_description', '2001-01-01', 100), " +
                "(2, 'film_two_name', 'film_two_description', '2002-02-02', 200), " +
                "(3, 'film_three_name', 'film_three_description', '2003-03-03', 300);");
    }

    @AfterEach
    void clearDb() {
        jdbcTemplate.execute("DELETE FROM users; " +
                "ALTER TABLE users " +
                "ALTER COLUMN user_id " +
                "RESTART WITH 1; " +
                "DELETE FROM films;" +
                "ALTER TABLE films " +
                "ALTER COLUMN film_id " +
                "RESTART WITH 1;");
    }

    @Test
    void putRate_normal_behavior() {
        rateStorage.putRate(1, 2);
        assertEquals(filmStorage.getPopularFilms(1), List.of(createSomeFilms().get(0)),
                "Received wrong film!");
    }

    @Test
    void putRate_wrong_friendId_throws_itemNotPresentException() {
        try {
            rateStorage.putRate(1, 123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no user with 123 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    @Test
    void putRate_wrong_filmId_throws_itemNotPresentException() {
        try {
            rateStorage.putRate(123, 1);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no film with 123 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    @Test
    void deleteRate_normal_behavior() {
        jdbcTemplate.execute("INSERT INTO rates (film_id, user_id) " +
                "VALUES " +
                "(1, 1), " +
                "(1, 2), " +
                "(1, 3), " +
                "(3, 1), " +
                "(3, 2);");
        rateStorage.deleteRate(1, 1);
        rateStorage.deleteRate(1, 2);
        assertEquals(List.of(createSomeFilms().get(2), createSomeFilms().get(0), createSomeFilms().get(1)),
                filmStorage.getPopularFilms(10),
                "Received wrong film list!");
    }

    @Test
    void deleteRate_wrong_userId_throws_itemNotPresentException() {
        try {
            rateStorage.deleteRate(1, 123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no film with 1 id or no user with 123 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    @Test
    void deleteRate_wrong_filmId_throws_itemNotPresentException() {
        try {
            rateStorage.deleteRate(123, 1);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no film with 123 id or no user with 1 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    private List<Film> createSomeFilms() {

        Film filmOne = new Film();
        filmOne.setId(1);
        filmOne.setMpa(new Mpa(1, "G"));
        filmOne.setName("film_one_name");
        filmOne.setDescription("film_one_description");
        filmOne.setDuration(100L);
        filmOne.setGenres(new HashSet<>());
        filmOne.setReleaseDate(LocalDate.of(2001, 1, 1));

        Film filmTwo = new Film();
        filmTwo.setId(2);
        filmTwo.setMpa(new Mpa(2, "PG"));
        filmTwo.setName("film_two_name");
        filmTwo.setDescription("film_two_description");
        filmTwo.setDuration(200L);
        filmTwo.setGenres(new HashSet<>());
        filmTwo.setReleaseDate(LocalDate.of(2002, 2, 2));

        Film filmThree = new Film();
        filmThree.setId(3);
        filmThree.setMpa(new Mpa(3, "PG-13"));
        filmThree.setName("film_three_name");
        filmThree.setDescription("film_three_description");
        filmThree.setDuration(300L);
        filmThree.setGenres(new HashSet<>());
        filmThree.setReleaseDate(LocalDate.of(2003, 3, 3));

        return List.of(filmOne, filmTwo, filmThree);
    }

}
