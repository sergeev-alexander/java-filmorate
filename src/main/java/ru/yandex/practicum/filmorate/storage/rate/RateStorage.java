package ru.yandex.practicum.filmorate.storage.rate;

public interface RateStorage {

    void putRate(Integer filmId, Integer userId);

    void deleteRate(Integer filmId, Integer userId);

}
