package com.practice.filmorate.storage.impl;

import com.practice.filmorate.exception.FilmNotFoundException;
import com.practice.filmorate.exception.InvalidReleaseDateException;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.storage.FilmStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

// InMemoryFilmStorage и InMemoryUserStorage, имплементирующие новые интерфейсы, и перенесите туда всю логику хранения,
// обновления и поиска объектов.
// Добавьте к InMemoryFilmStorage и InMemoryUserStorage аннотацию @Component, чтобы впоследствии пользоваться внедрением
// зависимостей и передавать хранилища сервисам.

@Component
@Slf4j

public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int uniqueId = 1;

    // Новая константа - минимальная допустимая дата релиза
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(int id) {
        for (Film film : films.values()) {
            if (film.getId() == id) {
                return Optional.of(film);
            }
        }
        return Optional.empty();
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film.getReleaseDate());     // метод-проверка, всё ли ок с датой релиза
        film.setId(getUniqueId());
        log.info("Создание фильма: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с данным id (" + film.getId() + ") не найден");
        }
        validateReleaseDate(film.getReleaseDate());     // метод-проверка, всё ли ок с датой релиза
        log.info("Обновление фильма: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с данным id (" + id + ") не найден");
        }
        log.info("Удаление фильма: {}", films.get(id));
        films.remove(id);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        // если дата релиза РАНЬШЕ допустимой - исключение
        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new InvalidReleaseDateException("Дата релиза не должна быть раньше 28 декабря 1895 года");
        }
    }

    private int getUniqueId() {
        return uniqueId++;
    }
}