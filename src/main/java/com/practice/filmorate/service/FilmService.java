package com.practice.filmorate.service;

import com.practice.filmorate.exception.*;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.model.Genre;
import com.practice.filmorate.storage.FilmStorage;
import com.practice.filmorate.storage.GenreStorage;
import com.practice.filmorate.storage.MpaStorage;
import com.practice.filmorate.storage.UserStorage;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

// FilmService, который будет отвечать за операции с фильмами, — добавление и удаление лайка, вывод 10 наиболее
// популярных фильмов по количеству лайков. Пусть пока каждый пользователь может поставить лайк фильму только один раз.

@Service
@Slf4j

public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    // Новая константа - минимальная допустимая дата релиза
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    // GET - СПИСОК ВСЕХ ФИЛЬМОВ
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    // GET - КОНКРЕТНЫЙ ФИЛЬМ (ПО id)
    public Film findById(int id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> {
                    log.error("Фильм с данным id (" + id + ") не найден");
                    return new FilmNotFoundException("Фильм с данным id (" + id + ") не найден");
                });
    }

    // GET - СПИСОК ПОПУЛЯРНЫХ ФИЛЬМОВ
    public List<Film> findPopular(int count) {
        return filmStorage.findPopular(count);
    }

    // POST - НОВЫЙ ФИЛЬМ
    public Film create(Film film) {
        validateFilm(film);
        if (film.getMpa() != null && !mpaStorage.existsById(film.getMpa().getId())) {
            throw new MpaNotFoundException("MPA с id " + film.getMpa().getId() + " не найден");
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (!genreStorage.existsById(genre.getId())) {
                    throw new GenreNotFoundException("Жанр с id " + genre.getId() + " не найден");
                }
            }
        }
        log.info("Добавлен новый фильм: {}", film.getName());
        return filmStorage.create(film);
    }

    // PUT - ОБНОВИТЬ ДАННЫЕ ФИЛЬМА
    public Film update(Film film) {
        validateFilm(film);
        if (!filmStorage.existsById(film.getId())) {
            log.error("Фильм по данному id не найден: {}", film.getId());
            throw new FilmNotFoundException("Фильм по данному id не найден");
        }
        log.info("Обновлён фильм: {}", film.getName());
        return filmStorage.update(film);
    }

    // PUT - ПОЛЬЗОВАТЕЛЬ СТАВИТ ЛАЙК ФИЛЬМУ
    public Film addLike(int filmId, int userId) {
        if (!filmStorage.existsById(filmId)) {
            log.error("Фильм по данному id не найден: {}", filmId);
            throw new FilmNotFoundException("Фильм по данному id не найден");
        }
        if (!userStorage.existsById(userId)) {
            log.error("Пользователь по данному id не найден: {}", userId);
            throw new UserNotFoundException("User not found");
        }
        log.info("Пользователь {} поставил лайк на фильм {}", userId, filmId);
        Film film = findById(filmId);
        film.getLikes().add(userId);
        return filmStorage.update(film);
//        String sql = "insert into film_likes (film_id, user_id) values (?, ?)";
//        jdbcTemplate.update(sql, filmId, userId);
//        return filmStorage.findById(filmId)
//                .orElseThrow(() -> new FilmNotFoundException("Фильм не найден"));
    }

    // DELETE - ФИЛЬМ
    public void delete(int id) {
        filmStorage.deleteFilm(id);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЬ УБИРАЕТ ЛАЙК С ФИЛЬМА
    public Film removeLike(int filmId, int userId) {
        if (!filmStorage.existsById(filmId)) {
            log.error("Фильм по данному id не найден: {}", filmId);
            throw new FilmNotFoundException("Фильм по данному id не найден");
        }
        if (!userStorage.existsById(userId)) {
            log.error("Пользователь по данному id не найден: {}", userId);
            throw new UserNotFoundException("User not found");
        }
        log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
        Film film = findById(filmId);
        film.getLikes().remove(userId);
        return filmStorage.update(film);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма пустое");
            throw new ValidationException("Поле name не должно быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Описание фильма превышает лимит в 200 символов");
            throw new ValidationException("Поле description не должно превышать 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Дата релиза раньше 28 декабря 1895 года");
            throw new InvalidReleaseDateException("Некорректная дата релиза");
        }
        if (film.getDuration() <= 0) {
            log.error("Некорректная продолжительность фильма");
            throw new ValidationException("Duration должна быть положительной");
        }
    }
}