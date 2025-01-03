package com.practice.filmorate.service;

import com.practice.filmorate.exception.FilmNotFoundException;
import com.practice.filmorate.exception.IncorrectParameterException;
import com.practice.filmorate.exception.UserNotFoundException;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.storage.FilmStorage;
import com.practice.filmorate.storage.UserStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// FilmService, который будет отвечать за операции с фильмами, — добавление и удаление лайка, вывод 10 наиболее
// популярных фильмов по количеству лайков. Пусть пока каждый пользователь может поставить лайк фильму только один раз.

@Service
@Slf4j

public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    // КОНСТРУКТОР
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
    public Optional<Film> findById(int id) {
        return Optional.ofNullable(filmStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с данным id (" + id + ") не найден")));
    }

    // GET - СПИСОК ПОПУЛЯРНЫХ ФИЛЬМОВ
    // Реализацию делала не сама. Нужно повторить Comparator/Comporable

    public List<Film> findAllPopular(int count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.findAll());
        allFilms.sort(new Comparator<Film>() {
            @Override
            public int compare(Film film1, Film film2) {
                return Integer.compare(film2.getLikes().size(), film1.getLikes().size());
            }
        });
        if (count > allFilms.size()) {
            count = allFilms.size();
        }
        return allFilms.subList(0, count);
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
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с данным id (" + filmId + ") не найден"));
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + userId + ") не найден"));
        film.getLikes().add(userId);
        return filmStorage.update(film);
    }

    // DELETE - ФИЛЬМ
    public void delete(int id) {
        filmStorage.delete(id);
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