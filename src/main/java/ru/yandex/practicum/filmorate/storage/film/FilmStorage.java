package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilmById(Integer filmId);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(Integer count);

    Film postFilm(Film film);

    Film putFilm(Film film);

}
