package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationHendle(MethodArgumentNotValidException e) {
        Map<String, String> response = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            response.put(error.getField(), error.getDefaultMessage());
        }
        log.error("Validation error: {}", response.entrySet());
        return response;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> itemNotPresentHandle(NotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("id", e.getMessage());
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return response;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> typeMismatchHandle(MethodArgumentTypeMismatchException e) {
        Map<String, String> response = new HashMap<>();
        log.error("{} : {}", e.getClass().getSimpleName(), e.getParameter().getParameter().getName());
        response.put(e.getParameter().getParameter().getName(), "Parameter type mismatch!");
        return response;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> constraintViolationHandle(ConstraintViolationException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
