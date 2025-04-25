package com.practice.filmorate.controller;

import com.practice.filmorate.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(FilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleFilmNotFound(FilmNotFoundException e) {
        return new ExceptionResponse(e.getMessage());               // "Ошибка поиска фильма"
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleUserNotFound(UserNotFoundException e) {
        return new ExceptionResponse(e.getMessage());               // "Ошибка поиска пользователя"
    }

    @ExceptionHandler(InvalidReleaseDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleInvalidReleaseDate(InvalidReleaseDateException e) {
        return new ExceptionResponse(e.getMessage());               // "Дата релиза не должна быть раньше 28 декабря 1895 года"
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleValidationErrors(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return new ExceptionResponse(errorMessage);
    }


}

//    @ExceptionHandler(UserAlreadyExistsException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ExceptionResponse handleUserAlreadyExist(UserAlreadyExistsException e) {
//        return new ExceptionResponse(e.getMessage());               // "Пользователь с данным email уже существует"
//    }
//
//    @ExceptionHandler(InvalidEmailException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ExceptionResponse handleInvalidEmail(InvalidEmailException e) {
//        return new ExceptionResponse(e.getMessage());               // "Пользователь с данным email уже существует"
//    }
//
//    @ExceptionHandler(IncorrectParameterException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ExceptionResponse handleIncorrectParameter(IncorrectParameterException e) {
//        return new ExceptionResponse(e.getMessage());                   // "Ошибка в переданных параметрах"
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ExceptionResponse handleValidationExceptions(MethodArgumentNotValidException e) {
//        return new ExceptionResponse("Проверка валидации не пройдена");
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ExceptionResponse handleConstraintViolationExceptions(ConstraintViolationException e) {
//        return new ExceptionResponse(e.getMessage());
//    }
//
//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ExceptionResponse handleServerError(Throwable e) {
//        return new ExceptionResponse("Произошла непредвиденная ошибка");
//    }

