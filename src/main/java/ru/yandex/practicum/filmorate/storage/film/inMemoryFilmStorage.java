package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class inMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> filmMap = new HashMap<>();
    private Integer filmIdCounter = 1;

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        presenceCheck(id);
        return filmMap.get(id);
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
        presenceCheck(film.getId());
        filmMap.put(film.getId(), film);
        log.info("Updated film: {}", film);
        return film;
    }

    public void presenceCheck(Integer id) {
        if (id == null || !filmMap.containsKey(id)) {
            log.error("Film validation error : There's no film with {} id!", id);
            throw new ItemNotPresentException("There's no film with " + id + " id!");
        }
    }

}
