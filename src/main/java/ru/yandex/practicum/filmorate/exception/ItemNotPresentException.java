package ru.yandex.practicum.filmorate.exception;

public class ItemNotPresentException extends RuntimeException {

    public ItemNotPresentException(String message) {
        super(message);
    }

}
