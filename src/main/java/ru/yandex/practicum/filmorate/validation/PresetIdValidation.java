package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = PresentIdValidator.class)
@Documented
public @interface PresetIdValidation {

    String message() default "Default PresentValidation message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    IdBelonging value();

    @Target(FIELD)
    @Retention(RUNTIME)
    @Documented
    @interface List {
        PresetIdValidation[] value();
    }

}