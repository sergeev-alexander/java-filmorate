package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
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

    public void putLike(Integer id, Integer userId) {
        filmStorage.presenceCheck(id);
        userStorage.presenceCheck(userId);
        filmStorage.getFilmById(id).getLikes().add(userId);
        log.info("User {} liked film {}", userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        filmStorage.presenceCheck(id);
        userStorage.presenceCheck(userId);
        filmStorage.getFilmById(id).getLikes().remove(userId);
        log.info("User {} unliked film {}", userId, id);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> filmList = filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        if (filmList.size() > count) {
            log.info("Showed popular films {}", filmList.subList(0, count));
            return filmList.subList(0, count);
        }
        log.info("Showed popular films {}", filmList);
        return filmList;
    }

}
