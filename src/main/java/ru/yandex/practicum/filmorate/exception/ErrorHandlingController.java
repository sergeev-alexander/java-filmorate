package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationHendle(MethodArgumentNotValidException e) {
        Map<String, String> response = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            response.put(error.getField(), error.getDefaultMessage());
        }
        Class<?> loggerPath = FilmorateApplication.class;
        if ("user".equals(e.getObjectName())) {
            loggerPath = UserController.class;
        } else if ("film".equals(e.getObjectName())) {
            loggerPath = FilmController.class;
        }
        Logger logger = LoggerFactory.getLogger(loggerPath);
        logger.error("{} validation error: {}", loggerPath.getSimpleName(), response.entrySet());
        return response;
    }

    @ExceptionHandler(ItemNotPresentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> itemNotPresentHandle(ItemNotPresentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("id", e.getMessage());
        return response;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> typeMismatchHandle(MethodArgumentTypeMismatchException e) {
        Map<String, String> response = new HashMap<>();
        Class<?> loggerPath = FilmorateApplication.class;
        if (Objects.requireNonNull(e.getParameter().getMethod()).getDeclaringClass().equals(FilmController.class)) {
            loggerPath = FilmController.class;
        }
        if (Objects.requireNonNull(e.getParameter().getMethod()).getDeclaringClass().equals(UserController.class)) {
            loggerPath = UserController.class;
        }
        Logger logger = LoggerFactory.getLogger(loggerPath);
        response.put(e.getParameter().getParameter().getName(), "Parameter type mismatch!");
        logger.error("Parameter type mismatch in {}.{}() field: {}",
                loggerPath.getSimpleName(),
                e.getParameter().getMethod().getName(),
                e.getParameter().getParameter().getName());
        return response;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> otherExceptonsHandle(MethodArgumentTypeMismatchException e) {
        return Map.of(e.getName(), String.valueOf(e.getCause()));
    }
}
