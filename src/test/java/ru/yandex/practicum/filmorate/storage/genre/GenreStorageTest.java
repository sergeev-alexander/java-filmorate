package ru.yandex.practicum.filmorate.storage.genre;

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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
class GenreStorageTest {

    JdbcTemplate jdbcTemplate;
    GenreStorage genreStorage;

    @Autowired
    public GenreStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        genreStorage = new GenreDbStorage(jdbcTemplate, new GenreMapper());
        jdbcTemplate.execute("INSERT INTO films (mpa_id, name, description, release_date, duration) " +
                "VALUES " +
                "(1, 'film_one_name', 'film_one_description', '2001-01-01', 100), " +
                "(2, 'film_two_name', 'film_two_description', '2002-02-02', 200), " +
                "(3, 'film_three_name', 'film_three_description', '2003-03-03', 300); " +
                "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES " +
                "(1, 1), " +
                "(1, 2), " +
                "(2, 2), " +
                "(3, 3);");
    }

    @AfterEach
    void clearDb() {
        jdbcTemplate.execute("DELETE FROM films;" +
                "ALTER TABLE films " +
                "ALTER COLUMN film_id " +
                "RESTART WITH 1;");
    }

    @Test
    void getGenreById_normal_behavior() {
        assertNotNull(genreStorage.getGenreById(1),
                "Received genre is null!");
        assertEquals(createGenres().get(0), genreStorage.getGenreById(1),
                "Received wrong genre!");
    }

    @Test
    void getGenreById_no_existing_genre_throws_itemNotPresentException() {
        try {
            genreStorage.getGenreById(123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Thrown wrong exception!");
            assertEquals("There's no genre with 123 id!", e.getMessage(),
                    "Exception has wrong message!");
        }
    }

    @Test
    void getAllGenres_normal_behavior() {
        assertNotNull(genreStorage.getAllGenres(),
                "Received set is null!");
        assertEquals(createGenres(), genreStorage.getAllGenres(),
                "Received wrong genres!");
    }

    @Test
    void getFilmGenresFromDb_normal_behavior() {
        assertNotNull(genreStorage.getFilmGenresFromDb(1),
                "Received genres set is null!");
        assertEquals(Set.of(createGenres().get(0), createGenres().get(1)), genreStorage.getFilmGenresFromDb(1),
                "Received wrong genres!");

    }

    @Test
    void setFilmGenresToDb_normal_behavior() {
        jdbcTemplate.execute("DELETE FROM film_genres;");
        genreStorage.setFilmGenresToDb(1, new HashSet<>(createGenres()));
        assertEquals(new HashSet<>(createGenres()), genreStorage.getFilmGenresFromDb(1),
                "Received wrong genres!");
    }

    @Test
    void setFilmGenresToDb_wrong_genreId_throws_itemNotPresentException() {
        jdbcTemplate.execute("DELETE FROM film_genres;");
        try {
            genreStorage.setFilmGenresToDb(1, Set.of(new Genre(123, "Some_genre")));
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Thrown wrong exception!");
            assertEquals("There's no genre with 123 id!", e.getMessage(),
                    "Exception has wrong message!");
        }
    }


    @Test
    void setGenresToFilmList_normal_behavior() {
        List<Film> receivedFilmList = genreStorage.setGenresToFilmList(createSomeFilms());
        assertEquals(receivedFilmList.get(0).getGenres(), Set.of(createGenres().get(0), createGenres().get(1)),
                "Received wrong genres!");
        assertEquals(receivedFilmList.get(1).getGenres(), Set.of(createGenres().get(1)),
                "Received wrong genres!");
        assertEquals(receivedFilmList.get(2).getGenres(), Set.of(createGenres().get(2)),
                "Received wrong genres!");
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
