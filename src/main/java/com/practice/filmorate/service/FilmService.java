package com.practice.filmorate.service;

import com.practice.filmorate.exception.FilmNotFoundException;
import com.practice.filmorate.exception.UserNotFoundException;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.storage.FilmStorage;
import com.practice.filmorate.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// FilmService, который будет отвечать за операции с фильмами, — добавление и удаление лайка, вывод 10 наиболее
// популярных фильмов по количеству лайков. Пусть пока каждый пользователь может поставить лайк фильму только один раз.

@Service
//@Slf4j

public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // GET - СПИСОК ВСЕХ ФИЛЬМОВ
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    // GET - КОНКРЕТНЫЙ ФИЛЬМ (ПО id)
    public Film findById(int id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с данным id (" + id + ") не найден"));
    }

    // GET - СПИСОК ПОПУЛЯРНЫХ ФИЛЬМОВ
    public List<Film> findAllPopular(int count) {
        return filmStorage.findAllPopular(count);
    }

    // POST - НОВЫЙ ФИЛЬМ
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    // PUT - ОБНОВИТЬ ДАННЫЕ ФИЛЬМА
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    // PUT - ПОЛЬЗОВАТЕЛЬ СТАВИТ ЛАЙК ФИЛЬМУ
    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с данным id (" + filmId + ") не найден"));
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + userId + ") не найден"));
        film.getLikes().add(userId);
        return filmStorage.update(film);
    }

    // DELETE - ФИЛЬМ
    public void delete(int id) {
        filmStorage.deleteFilm(id);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЬ УБИРАЕТ ЛАЙК С ФИЛЬМА
    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с данным id (" + filmId + ") не найден"));
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + userId + ") не найден"));
        film.getLikes().remove(userId);
        return filmStorage.update(film);
    }
}

// public List<Film> findAllPopular(int count) {
//    return findAll().stream()
//    .sorted(Comparator.comparing(Film film) -> film.getLikes().size().reversed())
//    .limit(count))
//    .toList();
//}