package ru.yandex.practicum.filmorate.storage.rate;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class RateDbStorage implements RateStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void putRate(Integer filmId, Integer userId) {
        jdbcTemplate.update("INSERT INTO rates (film_id, user_id) " +
                "VALUES " +
                "(?, ?);", filmId, userId);
    }

    @Override
    public void deleteRate(Integer filmId, Integer userId) {
        jdbcTemplate.update("DELETE FROM rates " +
                "WHERE film_id = ? " +
                "AND user_id = ?;", filmId, userId);
    }

}
