package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {


    private final Map<Integer, Film> filmMap = new HashMap<>();
    private Integer filmIdCounter = 1;

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @PostMapping("/films")
    public Film postFilm(@Valid @RequestBody Film film) {
        film.setId(filmIdCounter++);
        filmMap.put(film.getId(), film);
        log.info("Added new film: {}", film);
        return film;
    }

    @PutMapping("/films")
    public Film putFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null || !filmMap.containsKey(film.getId())) {
            log.error("Film validation error : There's no film with {} id!", film.getId());
            throw new ItemNotPresentException("There's no film with " + film.getId() + " id!");
        }
        filmMap.put(film.getId(), film);
        log.info("Updated film: {}", film);
        return film;
    }

}
