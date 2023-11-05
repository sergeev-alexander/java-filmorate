package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.IdBelonging;
import ru.yandex.practicum.filmorate.validation.PresetIdValidation;

import java.time.LocalDate;

@Data
public class User {

    @PresetIdValidation(message = "There's no user with such id!", value = IdBelonging.USER_ID)
    private Integer id;

    @NotBlank(message = "Email field is empty!")
    @Email(message = "Wrong email format!")
    private String email;

    @NotBlank(message = "Login field is empty!")
    @Pattern(regexp = "^\\S+$", message = "Login field should be without whitespaces!")
    private String login;

    private String name;

    @Past(message = "Birthday field must contain a past date!")
    private LocalDate birthday;

}
