package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
public class FilmController {


    private static final Map<Integer, Film> filmMap = new HashMap<>();
    private Integer filmIdCounter = 1;

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @PostMapping("/films")
    public Film postFilm(@Valid @RequestBody Film film) {
        film.setId(filmIdCounter++);
        filmMap.put(film.getId(), film);
        log.info("Added new film: " + film);
        return film;
    }

    @PutMapping("/films")
    public Film putFilm(@Valid @RequestBody Film film) {
        filmMap.put(film.getId(), film);
        log.info("Updated film: " + film);
        return film;
    }

    public static boolean isFilmPresent(Integer id) {
        return filmMap.containsKey(id);
    }

}
