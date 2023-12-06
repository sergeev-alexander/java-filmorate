package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class FilmService {

    @Autowired
    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        return Optional.of(filmStorage.getFilmById(id)).orElseThrow(
                () -> new ItemNotPresentException("There's no film with " + id + " id!"));
    }

    public Set<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(Integer genreId) {
        return filmStorage.getGenreById(genreId);
    }

    public Set<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer mpaId) {
        return filmStorage.getMpaById(mpaId);
    }

    public Film postFilm(Film film) {
        return filmStorage.postFilm(film);
    }

    public Film putFilm(Film film) {
        return filmStorage.putFilm(film);
    }

    public void putRate(Integer filmId, Integer userId) {
        filmStorage.putRate(filmId, userId);
    }

    public void deleteRate(Integer filmId, Integer userId) {
        filmStorage.deleteRate(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(film -> film.getRates().size(), Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());
    }

}
