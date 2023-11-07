package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {

    private Integer id;

    @NotBlank(message = "Name field is empty!")
    private String name;

    @NotNull
    @Size(max = 200, message = "Description field must be less 200 characters!")
    private String description;

    @ReleaseDateValidation(message = "Release date is before 28.12.1895!")
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Duration field must be positive!")
    private Long duration;

}
