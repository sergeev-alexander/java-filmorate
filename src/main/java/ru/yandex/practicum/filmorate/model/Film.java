package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validation.IdBelonging;
import ru.yandex.practicum.filmorate.validation.PresetIdValidation;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidation;

import java.time.LocalDate;

@Validated
@Data
public class Film {

    @PresetIdValidation(message = "There's no film with such id!", value = IdBelonging.FILM_ID)
    private Integer id;

    @NotBlank(message = "Name field is empty!")
    private String name;

    @Size(max = 200, message = "Description field must be less 200 characters!")
    private String description;

    @ReleaseDateValidation(message = "Release date is before 28.12.1895!")
    private LocalDate releaseDate;

    @Positive(message = "Duration field must be positive!")
    private Long duration;

}
