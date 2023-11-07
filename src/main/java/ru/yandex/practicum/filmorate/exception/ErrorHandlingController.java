package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> autoValidationHendle(MethodArgumentNotValidException e) {
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
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> itemNotPresentHandle(ItemNotPresentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("id", e.getMessage());
        return response;
    }

}
