package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilmById(Integer filmId);

    List<Film> getAllFilms();

    List<Film> getPopularFilmsByCount(Integer count);

    List<Film> getPopularFilmsByGenre(Integer count, Integer genreId);

    List<Film> getPopularFilmsByYear(Integer count, Integer year);

    List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year);

    Film postFilm(Film film);

    Film putFilm(Film film);

}
