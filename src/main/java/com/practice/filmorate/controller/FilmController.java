package com.practice.filmorate.controller;

import com.practice.filmorate.model.Film;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int uniqueId = 1;

    // Новая константа - минимальная допустимая дата релиза
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> get() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        validateReleaseDate(film.getReleaseDate());     // метод-проверка, всё ли ок с датой релиза
        film.setId(getUniqueId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new IllegalArgumentException("Фильм с данным id (" + film.getId() + ") не найден");
        }
        validateReleaseDate(film.getReleaseDate());     // метод-проверка, всё ли ок с датой релиза
        films.put(film.getId(), film);
        return film;
    }

    private int getUniqueId() {
        return uniqueId++;
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        // если дата релиза РАНЬШЕ допустимой - исключение
        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new IllegalArgumentException("Дата релиза не должна быть раньше 28 декабря 1895 года");
        }
    }

}
