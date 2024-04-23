package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public Film getFilmById(Integer filmId) {
        try {
            return jdbcTemplate.queryForObject("SELECT *, mpa_ratings.name AS mpa_name " +
                    "FROM films " +
                    "INNER JOIN mpa_ratings USING (mpa_id)" +
                    "WHERE film_id = ?;", filmMapper, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("There's no film with " + filmId + " id!");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT *, mpa_ratings.name AS mpa_name " +
                "FROM films " +
                "INNER JOIN mpa_ratings USING (mpa_id) ", filmMapper);
    }

    @Override
    public List<Film> getPopularFilmsByCount(Integer count) {
        return jdbcTemplate.query(
                "SELECT films.film_id, " +
                        "films.mpa_id, " +
                        "mpa_ratings.name AS mpa_name, " +
                        "films.name, " +
                        "films.description, " +
                        "films.release_date, " +
                        "films.duration " +
                        "FROM rates " +
                        "RIGHT JOIN films USING (film_id) " +
                        "INNER JOIN mpa_ratings USING (mpa_id) " +
                        "GROUP BY films.film_id " +
                        "ORDER BY COUNT(user_id) DESC " +
                        "LIMIT ?;", filmMapper, count);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(Integer count, Integer genreId) {
        return jdbcTemplate.query(
                "SELECT films.film_id, " +
                        "films.mpa_id, " +
                        "mpa_ratings.name AS mpa_name, " +
                        "films.name, " +
                        "films.description, " +
                        "films.release_date, " +
                        "films.duration " +
                        "FROM rates " +
                        "RIGHT JOIN films USING (film_id) " +
                        "INNER JOIN mpa_ratings USING (mpa_id) " +
                        "INNER JOIN film_genres USING (film_id) " +
                        "WHERE film_genres.genre_id = ? " +
                        "GROUP BY films.film_id " +
                        "ORDER BY COUNT(user_id) DESC " +
                        "LIMIT ?;", filmMapper, genreId, count);
    }

    @Override
    public List<Film> getPopularFilmsByYear(Integer count, Integer year) {
        return jdbcTemplate.query(
                "SELECT films.film_id, " +
                        "films.mpa_id, " +
                        "mpa_ratings.name AS mpa_name, " +
                        "films.name, " +
                        "films.description, " +
                        "films.release_date, " +
                        "films.duration " +
                        "FROM rates " +
                        "RIGHT JOIN films USING (film_id) " +
                        "INNER JOIN mpa_ratings USING (mpa_id) " +
                        "WHERE EXTRACT(YEAR FROM release_date) = ? " +
                        "GROUP BY films.film_id " +
                        "ORDER BY COUNT(user_id) DESC " +
                        "LIMIT ?;", filmMapper, year, count);
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        return jdbcTemplate.query(
                "SELECT films.film_id, " +
                        "films.mpa_id, " +
                        "mpa_ratings.name AS mpa_name, " +
                        "films.name, " +
                        "films.description, " +
                        "films.release_date, " +
                        "films.duration " +
                        "FROM rates " +
                        "RIGHT JOIN films USING (film_id) " +
                        "INNER JOIN mpa_ratings USING (mpa_id) " +
                        "INNER JOIN film_genres USING (film_id) " +
                        "WHERE EXTRACT(YEAR FROM release_date) = ? " +
                        "AND film_genres.genre_id = ? " +
                        "GROUP BY films.film_id " +
                        "ORDER BY COUNT(user_id) DESC " +
                        "LIMIT ?;", filmMapper, year, genreId, count);
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
        return film;
    }

    @Override
    public Film putFilm(Film film) {
        String query = "UPDATE films SET " +
                "name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa_id = ? " +
                "WHERE film_id = ?;";
        int updatedRowCount = jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (updatedRowCount == 0) {
            throw new NotFoundException("There's no film with " + film.getId() + " id!");
        }
        return film;
    }

}
