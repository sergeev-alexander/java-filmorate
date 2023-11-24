package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> filmMap = new HashMap<>();
    private Integer filmIdCounter = 1;

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        return Optional.ofNullable(filmMap.get(id)).orElseThrow(
                () -> new ItemNotPresentException("There's no film with " + id + " id!"));
    }

    @Override
    public Film postFilm(Film film) {
        film.setId(filmIdCounter++);
        filmMap.put(film.getId(), film);
        log.info("Added new film: {}", film);
        return film;
    }

    @Override
    public Film putFilm(Film film) {
        if (film.getId() == null) {
            log.error("Film validation error : Film has an empty id!");
            throw new ItemNotPresentException("Film has an empty id!");
        }
        getFilmById(film.getId());
        filmMap.put(film.getId(), film);
        log.info("Updated film: {}", film);
        return film;
    }

}
