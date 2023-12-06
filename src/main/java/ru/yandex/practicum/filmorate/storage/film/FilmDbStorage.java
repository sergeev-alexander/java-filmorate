package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final MpaMapper mpaMapper;
    private final GenreMapper genreMapper;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMapper filmMapper, MpaMapper mpaMapper,
                         GenreMapper genreMapper, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
        this.mpaMapper = mpaMapper;
        this.genreMapper = genreMapper;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM films", filmMapper).stream()
                .peek(film -> film.setRates(getFilmRatesFromDb(film.getId())))
                .peek(film -> film.setMpa(getFilmMpaFromDb(film.getId())))
                .peek(film -> film.setGenres(getFilmGenresFromDb(film.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Integer filmId) {
        try {
            Film film = jdbcTemplate.queryForObject("SELECT * FROM films " +
                    "WHERE film_id = " + filmId, filmMapper);
            film.setRates(getFilmRatesFromDb(filmId));
            film.setMpa(getFilmMpaFromDb(filmId));
            film.setGenres(getFilmGenresFromDb(filmId));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotPresentException("There's no film with " + filmId + " id!");
        }
    }

    @Override
    public Set<Genre> getAllGenres() {
        return new HashSet<>(jdbcTemplate.query("SELECT * FROM genres", genreMapper));
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genres " +
                    "WHERE genre_id = " + genreId, genreMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotPresentException("There's no genre with " + genreId + " id!");
        }
    }

    @Override
    public Set<Mpa> getAllMpa() {
        return new HashSet<>(jdbcTemplate.query("SELECT * " +
                "FROM mpa_ratings", mpaMapper));
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM mpa_ratings " +
                    "WHERE mpa_id = " + mpaId, mpaMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotPresentException("There's no mpa with " + mpaId + " id!");
        }
    }

    @Override
    public Film postFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());
        int filmId = (int) simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId(filmId);
        setFilmGenresToDb(filmId, film.getGenres());
        setFilmRatesToDb(filmId, film.getRates());
        film.setMpa(getFilmMpaFromDb(filmId));
        film.setGenres(getFilmGenresFromDb(filmId));
        return film;
    }

    @Override
    public Film putFilm(Film film) {
        getFilmById(film.getId());
        jdbcTemplate.execute("UPDATE films SET " +
                "name = '" + film.getName() + "', " +
                "description = '" + film.getDescription() + "', " +
                "release_date = '" + film.getReleaseDate() + "', " +
                "duration = '" + film.getDuration() + "', " +
                "mpa_id = '" + film.getMpa().getId() + "' " +
                "WHERE film_id = " + film.getId());
        setFilmRatesToDb(film.getId(), film.getRates());
        setFilmGenresToDb(film.getId(), film.getGenres());
        film.setGenres(getFilmGenresFromDb(film.getId()));
        return film;
    }

    @Override
    public void putRate(Integer filmId, Integer userId) {
        getFilmById(filmId);
        userDbStorage.getUserById(userId);
        jdbcTemplate.execute("INSERT INTO rates " +
                "VALUES " +
                "(" + filmId + ", " + userId + ");");
    }

    @Override
    public void deleteRate(Integer filmId, Integer userId) {
        getFilmById(filmId);
        userDbStorage.getUserById(userId);
        jdbcTemplate.execute("DELETE FROM rates " +
                "WHERE film_id = " + filmId + " " +
                "AND user_id = " + userId + ";");
    }

    public Set<Genre> getFilmGenresFromDb(Integer filmId) {
        String query = "SELECT * FROM genres " +
                "INNER JOIN film_genres USING (genre_id) " +
                "WHERE film_genres.film_id = " + filmId + " " +
                "ORDER BY genre_id";
        return new HashSet<>(jdbcTemplate.query(query, genreMapper));
    }

    public void setFilmGenresToDb(Integer filmId, Set<Genre> genreSet) {
        getFilmById(filmId);
        jdbcTemplate.execute("DELETE FROM film_genres " +
                "WHERE film_id = " + filmId);
        if (genreSet == null || genreSet.isEmpty()) {
            return;
        }
        for (Genre genre : genreSet) {
            getGenreById(genre.getId());
            jdbcTemplate.execute("INSERT INTO film_genres " +
                    "VALUES " +
                    "(" + filmId + ", " + genre.getId() + ");");
        }
    }

    public Set<Integer> getFilmRatesFromDb(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query("SELECT user_id " +
                        "FROM rates " +
                        "WHERE film_id = " + filmId,
                (resultSet, rowNum) -> resultSet.getInt("user_id")));
    }

    public void setFilmRatesToDb(Integer filmId, Set<Integer> rates) {
        getFilmById(filmId);
        jdbcTemplate.execute("DELETE FROM rates " +
                "WHERE film_id = " + filmId);
        if (rates == null || rates.isEmpty()) {
            return;
        }
        rates.forEach(userDbStorage::getUserById);
        for (Integer userId : rates) {
            jdbcTemplate.execute("INSERT INTO rates " +
                    "VALUES " +
                    "(" + filmId + ", " + userId + ");");
        }
    }

    public Mpa getFilmMpaFromDb(Integer filmId) {
        return jdbcTemplate.queryForObject("SELECT * " +
                "FROM mpa_ratings " +
                "INNER JOIN films USING (mpa_id) " +
                "WHERE films.film_id = " + filmId + ";", mpaMapper);
    }

}
