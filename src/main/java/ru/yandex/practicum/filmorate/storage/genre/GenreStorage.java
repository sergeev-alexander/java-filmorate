package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    Genre getGenreById(Integer genreId);

    List<Genre> getAllGenres();

    Set<Genre> getFilmGenresFromDb(Integer filmId);

    void setFilmGenresToDb(Integer filmId, Set<Genre> genreSet);

    List<Film> setGenresToFilmList(List<Film> filmList);

}
