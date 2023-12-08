package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public Film getFilmById(Integer filmId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM films " +
                    "WHERE film_id = " + filmId, filmMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotPresentException("There's no film with " + filmId + " id!");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM films", filmMapper);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Integer> filmIds = getPopularFilmsIds(count);
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        return new LinkedList<>(jdbcTemplate.query(String.format("SELECT * " +
                        "FROM films " +
                        "WHERE film_id IN (%s);", inSql), filmIds.toArray(),
                filmMapper));
    }

    private List<Integer> getPopularFilmsIds(Integer count) {
        return jdbcTemplate.query("SELECT films.film_id " +
                "FROM films " +
                "LEFT JOIN rates USING (film_id) " +
                "GROUP BY films.film_id " +
                "ORDER BY COUNT(user_id) DESC " +
                "LIMIT " + count, (resultSet, rowNum) -> resultSet.getInt("film_id"));
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
        getFilmById(film.getId());
        String query = "UPDATE films SET " +
                "name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa_id = ? " +
                "WHERE film_id = ?;";
        jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

}
