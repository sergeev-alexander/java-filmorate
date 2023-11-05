package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    public Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validUserValidation() {
        User user = new User();
        user.setEmail("mail@maiil.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        assertTrue(violationSet.isEmpty(),
                "Valid film didn't pass the validation!");
    }

    @Test
    void notValidUserValidation() {
        User user = new User();
        user.setId(123);
        user.setEmail("wrong&email");
        user.setLogin("wrong login");
        user.setBirthday(LocalDate.of(2345, 1, 1));

        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        List<String> messageTemplates = new ArrayList<>();
        for (ConstraintViolation<User> violation : violationSet) {
            messageTemplates.add(violation.getMessageTemplate());
        }
        assertTrue(messageTemplates.contains("There's no user with such id!"),
                "Id validation failed!");
        assertTrue(messageTemplates.contains("Wrong email format!"),
                "Email validation failed!");
        assertTrue(messageTemplates.contains("Login field should be without whitespaces!"),
                "Login validation failed!");
        assertTrue(messageTemplates.contains("Birthday field must contain a past date!"),
                "Birthday date validation failed!");
    }

}
