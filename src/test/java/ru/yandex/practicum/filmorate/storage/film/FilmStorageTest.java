package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class FilmStorageTest {

    public final JdbcTemplate jdbcTemplate;
    public FilmStorage filmStorage;

    @Autowired
    FilmStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        filmStorage = new FilmDbStorage(jdbcTemplate, new FilmMapper());
        jdbcTemplate.execute("INSERT INTO films (mpa_id, name, description, release_date, duration) " +
                "VALUES " +
                "(1, 'film_one_name', 'film_one_description', '2001-01-01', 100), " +
                "(2, 'film_two_name', 'film_two_description', '2002-02-02', 200), " +
                "(3, 'film_three_name', 'film_three_description', '2003-03-03', 300);");
    }

    @AfterEach
    void clearDb() {
        jdbcTemplate.execute("DELETE FROM films;" +
                "ALTER TABLE films " +
                "ALTER COLUMN film_id " +
                "RESTART WITH 1;");
    }

    @Test
    void getFilmById_normal_behavior() {
        assertEquals(createSomeFilms().get(0), filmStorage.getFilmById(1),
                "Returned wrong film!");
        assertEquals(createSomeFilms().get(1), filmStorage.getFilmById(2),
                "Returned wrong film!");
        assertEquals(createSomeFilms().get(2), filmStorage.getFilmById(3),
                "Returned wrong film!");
    }

    @Test
    void getFilmById_not_existing_id_throws_itemNotPresentException() {
        try {
            filmStorage.getFilmById(123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Thrown wrong exception!");
            assertEquals("There's no film with 123 id!", e.getMessage(),
                    "Exception has wrong message!");
        }
    }

    @Test
    void getAllFilms_normal_behavior() {
        assertNotNull(filmStorage.getAllFilms(),
                "Received list is null!");
        assertEquals(createSomeFilms(), filmStorage.getAllFilms(),
                "Received films are wrong!");
    }

    @Test
    void getAllFilms_no_films_return_empty_list() {
        clearDb();
        assertNotNull(filmStorage.getAllFilms(),
                "Received list is null!");
        assertTrue(filmStorage.getAllFilms().isEmpty(),
                "Received list is not empty!");
    }

    @Test
    void getPopularFilms_normal_behavior() {
        jdbcTemplate.execute("INSERT INTO users (name, email, login, birthday) " +
                "VALUES " +
                "('name_one', 'email@one.com', 'login_one', '2001-01-01'), " +
                "('name_two', 'email@two.com', 'login_two', '2002-02-02'), " +
                "('name_three', 'email@three.com', 'login_three', '2003-03-03'); " +
                "INSERT INTO rates (film_id, user_id) " +
                "VALUES " +
                "(3, 1), " +
                "(3, 2), " +
                "(3, 3), " +
                "(1, 1), " +
                "(1, 2);");
        assertEquals(3, filmStorage.getPopularFilms(10).size(),
                "Received film list has wrong size!");
        assertEquals(filmStorage.getPopularFilms(1).get(0), createSomeFilms().get(2),
                "Received wrong film!");
        assertEquals(filmStorage.getPopularFilms(2).get(1), createSomeFilms().get(0),
                "Received wrong film!");
        assertEquals(filmStorage.getPopularFilms(3), List.of(createSomeFilms().get(2),
                        createSomeFilms().get(0), createSomeFilms().get(1)),
                "Received wrong film list!");
    }

    @Test
    void getPopularFilms_empty_rates_returns_all_films() {
        assertNotNull(filmStorage.getPopularFilms(10),
                "Received film list is null!");
        assertEquals(3, filmStorage.getPopularFilms(10).size(),
                "Received film list has wrong size!");

    }

    @Test
    void postFilm_normal_behavior() {
        clearDb();
        Film film = createSomeFilms().get(0);
        film.setId(null);
        assertEquals(1, filmStorage.postFilm(film).getId(),
                "Returned film id is wrong!");
        film.setId(1);
        assertEquals(film, filmStorage.getFilmById(1),
                "Received wrong film!");
    }

    @Test
    void putFilm_normal_behavior() {
        Film film = createSomeFilms().get(2);
        film.setId(1);
        assertEquals(film, filmStorage.putFilm(film),
                "Returned wrong film!");
        assertEquals(film, filmStorage.getFilmById(1),
                "Received wrong film!");
    }

    @Test
    void putFilm_not_existing_id_throws_itemNotPresentException() {
        Film film = createSomeFilms().get(0);
        film.setId(123);
        try {
            filmStorage.putFilm(film);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Thrown wrong exception!");
            assertEquals("There's no film with 123 id!", e.getMessage(),
                    "Exception has wrong message!");
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
