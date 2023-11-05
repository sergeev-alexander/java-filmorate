package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmControllerTest {

    private static final String BIG_TEXT = "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" +
            "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" +
            "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" +
            "1234567890" + "1234567890";
    public Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validFilmValidation() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 29));
        film.setDuration(123L);

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film);
        assertTrue(violationSet.isEmpty(),
                "Valid film didn't pass the validation!");

    }

    @Test
    void notValidFilmValidation() {
        Film film = new Film();
        film.setId(123);
        film.setName("");
        film.setDescription(BIG_TEXT);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(-123L);

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film);
        List<String> messageTemplates = new ArrayList<>();
        for (ConstraintViolation<Film> violation : violationSet) {
            messageTemplates.add(violation.getMessageTemplate());
        }
        assertTrue(messageTemplates.contains("There's no film with such id!"),
                "Id validation failed!");
        assertTrue(messageTemplates.contains("Name field is empty!"),
                "Name validation failed!");
        assertTrue(messageTemplates.contains("Description field must be less 200 characters!"),
                "Description validation failed!");
        assertTrue(messageTemplates.contains("Release date is before 28.12.1895!"),
                "Release date validation failed!");
        assertTrue(messageTemplates.contains("Duration field must be positive!"),
                "Duration validation failed!");

    }

}
