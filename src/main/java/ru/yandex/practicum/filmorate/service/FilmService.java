package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.rate.RateStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final RateStorage rateStorage;

    public List<Film> getAllFilms() {
        return genreStorage.setGenresToFilmList(filmStorage.getAllFilms());
    }

    public Film getFilmById(Integer filmId) {
        Film film = filmStorage.getFilmById(filmId);
        film.setGenres(genreStorage.getFilmGenresFromDb(filmId));
        return film;
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(Integer genreId) {
        return genreStorage.getGenreById(genreId);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer mpaId) {
        return mpaStorage.getMpaById(mpaId);
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Film> resultFilmlist;
        if (null != genreId && null != year) {
            resultFilmlist = filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
        } else if (null != genreId) {
            resultFilmlist = filmStorage.getPopularFilmsByGenre(count, genreId);
        } else if (null != year) {
            resultFilmlist = filmStorage.getPopularFilmsByYear(count, year);
        } else {
            resultFilmlist = filmStorage.getPopularFilmsByCount(count);
        }
        return genreStorage.setGenresToFilmList(resultFilmlist);
    }

    public Film postFilm(Film film) {
        Film resultFilm = filmStorage.postFilm(film);
        genreStorage.setFilmGenresToDb(resultFilm.getId(), resultFilm.getGenres());
        resultFilm.setGenres(genreStorage.getFilmGenresFromDb(resultFilm.getId()));
        resultFilm.setMpa(mpaStorage.getMpaById(resultFilm.getMpa().getId()));
        return resultFilm;
    }

    public Film putFilm(Film film) {
        Film resultFilm = filmStorage.putFilm(film);
        genreStorage.setFilmGenresToDb(resultFilm.getId(), resultFilm.getGenres());
        resultFilm.setGenres(genreStorage.getFilmGenresFromDb(resultFilm.getId()));
        return resultFilm;
    }

    public void putRate(Integer filmId, Integer userId) {
        rateStorage.putRate(filmId, userId);
    }

    public void deleteRate(Integer filmId, Integer userId) {
        rateStorage.deleteRate(filmId, userId);
    }

}
