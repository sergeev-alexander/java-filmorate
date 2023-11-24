package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotPresentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        return Optional.of(filmStorage.getFilmById(id)).orElseThrow(
                () -> new ItemNotPresentException("There's no film with " + id + " id!"));
    }

    public Film postFilm(Film film) {
        return filmStorage.postFilm(film);
    }

    public Film putFilm(Film film) {
        return filmStorage.putFilm(film);
    }

    public void putLike(Integer id, Integer userId) {
        userStorage.getUserById(userId);
        getFilmById(id).getLikes().add(userId);
        log.info("User {} liked film {}", userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        userStorage.getUserById(userId);
        getFilmById(id).getLikes().remove(userId);
        log.info("User {} unliked film {}", userId, id);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> filmList = filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());
        log.info("Showed popular films {}", filmList);
        return filmList;
    }

}
