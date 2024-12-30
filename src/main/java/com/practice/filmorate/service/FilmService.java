package com.practice.filmorate.service;

import com.practice.filmorate.model.Film;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.FilmStorage;
import com.practice.filmorate.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

// FilmService, который будет отвечать за операции с фильмами, — добавление и удаление лайка, вывод 10 наиболее
// популярных фильмов по количеству лайков. Пусть пока каждый пользователь может поставить лайк фильму только один раз.

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Optional<Film> findById(int id) {
        return Optional.ofNullable(filmStorage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Фильм с данным id (" + id + ") не найден")));
    }

    public void delete(int id) {
        filmStorage.delete(id);
    }

    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм с данным id (" + filmId + ") не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с данным id (" + userId + ") не найден"));
        film.getLikes().add(userId);
    }

    public void removeLike(@PathVariable int filmId, @PathVariable int userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм с данным id (" + filmId + ") не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с данным id (" + userId + ") не найден"));
        filmStorage.update(film);
    }

    public List<Film> findAllPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .toList();
    }
}