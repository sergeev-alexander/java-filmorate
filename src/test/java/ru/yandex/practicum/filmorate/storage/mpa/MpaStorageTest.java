package ru.yandex.practicum.filmorate.storage.mpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
class MpaStorageTest {

    JdbcTemplate jdbcTemplate;
    MpaStorage mpaStorage;

    @Autowired
    public MpaStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        mpaStorage = new MpaDbStorage(jdbcTemplate, new MpaMapper());
    }

    @Test
    void getMpaById_normal_behavior() {
        assertNotNull(mpaStorage.getMpaById(1),
                "Received mpa is null!");
        assertEquals(createMpas().get(0), mpaStorage.getMpaById(1),
                "Received wrong mpa!");
    }

    @Test
    void getMpaById_not_existing_id_throws_itemNotPresentException() {
        try {
            mpaStorage.getMpaById(123);
        } catch (ItemNotPresentException e) {
            assertEquals(ItemNotPresentException.class, e.getClass(),
                    "Thrown wrong exception!");
            assertEquals("There's no mpa with 123 id!", e.getMessage(),
                    "Exception has wrong message!");
        }
    }

    @Test
    void getAllMpa_normal_behavior() {
        assertNotNull(mpaStorage.getAllMpa(),
                "Received set is null!");
        assertEquals(createMpas(), mpaStorage.getAllMpa(),
                "Received wrong mpa_ratings!");
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
