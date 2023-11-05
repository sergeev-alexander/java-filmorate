package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

public class PresentIdValidator implements ConstraintValidator<PresetIdValidation, Integer> {

    private IdBelonging idBelonging;

    @Override
    public void initialize(PresetIdValidation constraintAnnotation) {
        this.idBelonging = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintValidatorContext) {
        if (id == null) {
            return true;
        }
        if (idBelonging == IdBelonging.FILM_ID) {
            return FilmController.isFilmPresent(id);
        } else {
            return UserController.isUserPresent(id);
        }
    }

}
