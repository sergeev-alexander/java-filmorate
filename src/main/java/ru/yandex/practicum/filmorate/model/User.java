package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private Integer id;

    @NotBlank(message = "Email field is empty!")
    @Email(message = "Wrong email format!")
    private String email;

    @NotBlank(message = "Login field is empty!")
    @Pattern(regexp = "^\\S+$", message = "Login field should be without whitespaces!")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent(message = "Birthday field must contain a past date!")
    private LocalDate birthday;

}
