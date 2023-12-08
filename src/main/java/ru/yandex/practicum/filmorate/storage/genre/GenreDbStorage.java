package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;

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
    public List<Genre> getAllGenres() {
        return new LinkedList<>(jdbcTemplate.query("SELECT * FROM genres", genreMapper));
    }

    @Override
    public Set<Genre> getFilmGenresFromDb(Integer filmId) {
        String query = "SELECT * FROM genres " +
                "INNER JOIN film_genres USING (genre_id) " +
                "WHERE film_genres.film_id = " + filmId + " " +
                "ORDER BY genre_id;";
        return new HashSet<>(jdbcTemplate.query(query, genreMapper));
    }

    @Override
    public void setFilmGenresToDb(Integer filmId, Set<Genre> genreSet) {
        jdbcTemplate.update("DELETE FROM film_genres " +
                "WHERE film_id = ?;", filmId);
        if (genreSet == null || genreSet.isEmpty()) {
            return;
        }
        List<Genre> genreList = new ArrayList<>(genreSet);
        jdbcTemplate.batchUpdate("INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?);", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, filmId);
                preparedStatement.setInt(2, genreList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genreList.size();
            }
        });
    }

    @Override
    public List<Film> setGenresToFilmList(List<Film> filmList) {
        Map<Integer, Film> filmMap = filmList.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String questionMarks = String.join(", ", (Collections.nCopies(filmMap.size(), "?")));
        String query = String.format("SELECT * " +
                "FROM film_genres " +
                "INNER JOIN genres USING (genre_id) " +
                "WHERE film_id IN (%s);", questionMarks);
        jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Film film = filmMap.get(resultSet.getInt("film_id"));
            film.addGenreToGenresSet(new Genre(resultSet.getInt("genre_id"), resultSet.getString("name")));
            return null;
        }, filmMap.keySet().toArray());
        return new LinkedList<>(filmMap.values());
    }

}
