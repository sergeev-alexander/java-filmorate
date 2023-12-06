package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class FilmStorageTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    FilmDbStorage filmDbStorage;

    @BeforeEach
    void setUp() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate, new FilmMapper(), new MpaMapper(), new GenreMapper(),
                new UserDbStorage(jdbcTemplate, new UserMapper()));
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
    void getAllFilms_normal_behavior() {
        assertNotNull(filmDbStorage.getAllFilms(),
                "Received list is null!");
        assertEquals(createSomeFilms(), filmDbStorage.getAllFilms(),
                "Received films are wrong!");
    }

    @Test
    void getAllFilms_no_films_return_empty_list() {
        clearDb();
        assertNotNull(filmDbStorage.getAllFilms(),
                "Received list is null!");
        assertTrue(filmDbStorage.getAllFilms().isEmpty(),
                "Received list is not empty!");
    }

    @Test
    void getFilmById_normal_behavior() {
        assertEquals(createSomeFilms().get(0), filmDbStorage.getFilmById(1),
                "Returned wrong film!");
        assertEquals(createSomeFilms().get(1), filmDbStorage.getFilmById(2),
                "Returned wrong film!");
        assertEquals(createSomeFilms().get(2), filmDbStorage.getFilmById(3),
                "Returned wrong film!");
    }

    @Test
    void getAllGenres_normal_behavior() {
        assertNotNull(filmDbStorage.getAllGenres(),
                "Received set is null!");
        assertEquals(createGenres(), filmDbStorage.getAllGenres(),
                "Received wrong genres!");
    }

    @Test
    void getGenreById_normal_behavior() {
        assertNotNull(filmDbStorage.getGenreById(1),
                "Received genre is null!");
        assertEquals(createGenres().get(0), filmDbStorage.getGenreById(1),
                "Received wrong genre!");
    }

    @Test
    void getGenreById_no_existing_genre_throws_itemNotPresentException() {
        try {
            filmDbStorage.getGenreById(123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Thrown wrong exception!");
            assertEquals("There's no genre with 123 id!", e.getMessage(),
                    "Exception has wrong message!");
        }
    }

    @Test
    void getAllMpa_normal_behavior() {
        assertNotNull(filmDbStorage.getAllMpa(),
                "Received set is null!");
        assertEquals(createMpas(), filmDbStorage.getAllMpa(),
                "Received wrong mpa_ratings!");
    }

    @Test
    void getMpaById_normal_behavior() {
        assertNotNull(filmDbStorage.getMpaById(1),
                "Received mpa is null!");
        assertEquals(createMpas().get(0), filmDbStorage.getMpaById(1),
                "Received wrong mpa!");
    }

    @Test
    void getMpaById_not_existing_id_throws_itemNotPresentException() {
        try {
            filmDbStorage.getMpaById(123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Thrown wrong exception!");
            assertEquals("There's no mpa with 123 id!", e.getMessage(),
                    "Exception has wrong message!");
        }
    }

    @Test
    void postFilm_normal_behavior() {
        clearDb();
        Film film = createSomeFilms().get(0);
        film.setId(null);
        film.setGenres(Set.of(createGenres().get(0)));
        film.setMpa(createMpas().get(0));
        assertEquals(1, filmDbStorage.postFilm(film).getId(),
                "Returned film id is wrong!");
        film.setId(1);
        assertEquals(film, filmDbStorage.getFilmById(1),
                "Received wrong film!");
    }

    @Test
    void putFilm_normal_behavior() {
        Film film = createSomeFilms().get(2);
        film.setId(1);
        assertEquals(film, filmDbStorage.putFilm(film),
                "Returned wrong film!");
        assertEquals(film, filmDbStorage.getFilmById(1),
                "Received wrong film!");
    }

    @Test
    void putFilm_not_existing_id_throws_itemNotPresentException() {
        Film film = createSomeFilms().get(0);
        film.setId(123);
        try {
            filmDbStorage.putFilm(film);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Thrown wrong exception!");
            assertEquals("There's no film with 123 id!", e.getMessage(),
                    "Exception has wrong message!");
        }
    }

    @Test
    void putRate_normal_behavior() {
        jdbcTemplate.execute("INSERT INTO users (name, email, login, birthday) " +
                "VALUES " +
                "('name_one', 'email@one.com', 'login_one', '2001-01-01'), " +
                "('name_two', 'email@two.com', 'login_two', '2002-02-02');");
        filmDbStorage.putRate(1, 2);
        assertFalse(filmDbStorage.getFilmById(1).getRates().isEmpty(),
                "Received empty rates!");
        assertEquals(filmDbStorage.getFilmById(1).getRates(), Set.of(2),
                "Received wrong rates!");
        jdbcTemplate.execute("DELETE FROM users;");
    }

    @Test
    void putRate_from_not_existing_user_throws_itemNotPresentException() {
        try {
            filmDbStorage.putRate(1, 1);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Was thrown wrong exception!");
            assertEquals("There's no user with 1 id!", e.getMessage(),
                    "Wrong exception message!");
        }
    }

    @Test
    void deleteRate_normal_behavior() {
        jdbcTemplate.execute("INSERT INTO users (name, email, login, birthday) " +
                "VALUES " +
                "('name_one', 'email@one.com', 'login_one', '2001-01-01'); " +
                "INSERT INTO rates " +
                "VALUES " +
                "(1, 1);");
        filmDbStorage.deleteRate(1, 1);
        assertTrue(filmDbStorage.getFilmById(1).getRates().isEmpty(),
                "Rate was not deleted!");
    }

    private List<Film> createSomeFilms() {

        Film filmOne = new Film();
        filmOne.setId(1);
        filmOne.setMpa(new Mpa(1, "G"));
        filmOne.setName("film_one_name");
        filmOne.setDescription("film_one_description");
        filmOne.setDuration(100L);
        filmOne.setGenres(new HashSet<>());
        filmOne.setRates(new HashSet<>());
        filmOne.setReleaseDate(LocalDate.of(2001, 1, 1));

        Film filmTwo = new Film();
        filmTwo.setId(2);
        filmTwo.setMpa(new Mpa(2, "PG"));
        filmTwo.setName("film_two_name");
        filmTwo.setDescription("film_two_description");
        filmTwo.setDuration(200L);
        filmTwo.setGenres(new HashSet<>());
        filmTwo.setRates(new HashSet<>());
        filmTwo.setReleaseDate(LocalDate.of(2002, 2, 2));

        Film filmThree = new Film();
        filmThree.setId(3);
        filmThree.setMpa(new Mpa(3, "PG-13"));
        filmThree.setName("film_three_name");
        filmThree.setDescription("film_three_description");
        filmThree.setDuration(300L);
        filmThree.setGenres(new HashSet<>());
        filmThree.setRates(new HashSet<>());
        filmThree.setReleaseDate(LocalDate.of(2003, 3, 3));

        return List.of(filmOne, filmTwo, filmThree);
    }

    private List<Genre> createGenres() {
        return List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик"));
    }

    private List<Mpa> createMpas() {
        return List.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17"));
    }

}
