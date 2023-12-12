package ru.yandex.practicum.filmorate.storage.rate;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;

@Repository
@AllArgsConstructor
public class RateDbStorage implements RateStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void putRate(Integer filmId, Integer userId) {
        try {
            jdbcTemplate.update("INSERT INTO rates (film_id, user_id) " +
                    "VALUES " +
                    "(?, ?);", filmId, userId);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (e.getMessage().contains("FOREIGN KEY(FILM_ID)")) {
                message = "There's no film with " + filmId + " id!";
            } else if (e.getMessage().contains("FOREIGN KEY(USER_ID)")) {
                message = "There's no user with " + userId + " id!";
            }
            throw new ItemNotPresentException(message);
        }
    }

    @Override
    public void deleteRate(Integer filmId, Integer userId) {
            int updatedRowCount = jdbcTemplate.update("DELETE FROM rates " +
                    "WHERE film_id = ? " +
                    "AND user_id = ?;", filmId, userId);
            if (updatedRowCount == 0) {
                throw new ItemNotPresentException("There's no film with " + filmId + " id " +
                        "or no user with " + userId + " id!");
        }
    }

}
