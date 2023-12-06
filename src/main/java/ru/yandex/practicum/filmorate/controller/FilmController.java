package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(
            @Positive(message = "Must be positive!")
            @RequestParam(required = false, defaultValue = "10")
            Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/genres")
    public Set<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("genres/{genreId}")
    public Genre getGenreById(@PathVariable Integer genreId) {
        return filmService.getGenreById(genreId);
    }

    @GetMapping("/mpa")
    public Set<Mpa> getAllMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("mpa/{mpaId}")
    public Mpa getMpaById(@PathVariable Integer mpaId) {
        return filmService.getMpaById(mpaId);
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
    public void putLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.putRate(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public void deleteRate(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.deleteRate(filmId, userId);
    }

}
