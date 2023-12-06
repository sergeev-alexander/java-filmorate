package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film getFilmById(Integer filmId);

    Set<Genre> getAllGenres();

    Genre getGenreById(Integer genreId);

    Set<Mpa> getAllMpa();

    Mpa getMpaById(Integer mpaId);

    Film postFilm(Film film);

    Film putFilm(Film film);

    void putRate(Integer filmId, Integer userId);

    void deleteRate(Integer filmId, Integer userId);

}
