package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

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
    public List<Mpa> getAllMpa() {
        return new LinkedList<>(jdbcTemplate.query("SELECT * " +
                "FROM mpa_ratings", mpaMapper));
    }

    @Override
    public Mpa getFilmMpaFromDb(Integer filmId) {
        return jdbcTemplate.queryForObject("SELECT * " +
                "FROM mpa_ratings " +
                "INNER JOIN films USING (mpa_id) " +
                "WHERE films.film_id = " + filmId + ";", mpaMapper);
    }

    @Override
    public List<Film> setMpaToFilmList(List<Film> filmList) {
        Map<Integer, Film> filmMap = filmList.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String questionMarks = String.join(",", Collections.nCopies(filmMap.size(), "?"));
        String query = String.format("SELECT * " +
                "FROM mpa_ratings " +
                "INNER JOIN films USING (mpa_id) " +
                "WHERE films.film_id IN (%s);", questionMarks);
        jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Film film = filmMap.get(resultSet.getInt("film_id"));
            film.setMpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("name")));
            return film;
        }, filmMap.keySet().toArray());
        return new LinkedList<>(filmMap.values());
    }

}
