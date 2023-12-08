package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    Mpa getMpaById(Integer mpaId);

    List<Mpa> getAllMpa();

    Mpa getFilmMpaFromDb(Integer filmId);

    List<Film> setMpaToFilmList(List<Film> filmList);

}
