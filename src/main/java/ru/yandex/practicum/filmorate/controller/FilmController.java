package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;



@RestController
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable @Positive(message = "Must be positive!") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") @Positive(message = "Must be positive!") Integer count,
            @RequestParam(required = false) @Positive(message = "Must be positive!") Integer genreId,
            @RequestParam(required = false) @Min(value = 1895) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @PostMapping("/films")
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.postFilm(film);
    }

    @PutMapping("/films")
    public Film putFilm(@Valid @RequestBody Film film) {
        return filmService.putFilm(film);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public void putRate(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.putRate(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public void deleteRate(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.deleteRate(filmId, userId);
    }

}
