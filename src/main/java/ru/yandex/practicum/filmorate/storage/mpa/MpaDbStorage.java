package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedList;
import java.util.List;

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

}
