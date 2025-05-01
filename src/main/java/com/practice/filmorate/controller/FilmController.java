package com.practice.filmorate.controller;

import com.practice.filmorate.model.Film;
import com.practice.filmorate.service.FilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/films")
@Validated

public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // GET - СПИСОК ВСЕХ ФИЛЬМОВ
    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    // GET - КОНКРЕТНЫЙ ФИЛЬМ (ПО id)
    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        return filmService.findById(id);
    }

    // GET - СПИСОК ПОПУЛЯРНЫХ ФИЛЬМОВ
    @GetMapping("/popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.findPopular(count);
    }

    // POST - НОВЫЙ ФИЛЬМ
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    // PUT - ОБНОВИТЬ ДАННЫЕ ФИЛЬМА
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    // PUT - ПОЛЬЗОВАТЕЛЬ СТАВИТ ЛАЙК ФИЛЬМУ
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    // DELETE - ФИЛЬМ
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.delete(id);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЬ УБИРАЕТ ЛАЙК С ФИЛЬМА
    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.removeLike(id, userId);
    }
}
