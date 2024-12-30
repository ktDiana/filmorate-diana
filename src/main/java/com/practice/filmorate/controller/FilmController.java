package com.practice.filmorate.controller;

import com.practice.filmorate.model.Film;
import com.practice.filmorate.service.FilmService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    // КОНСТРУКТОР
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // GET - СПИСОК ВСЕХ ФИЛЬМОВ
    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    // GET - СПИСОК ПОПУЛЯРНЫХ ФИЛЬМОВ
    @GetMapping("/popular?count={count}")
    public List<Film> findAllPopular(@PathVariable int count) {
        return filmService.findAllPopular(count);
    }

    // POST - НОВЫЙ ФИЛЬМ
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    // PUT - ОБНОВИТЬ ДАННЫЕ ФИЛЬМА
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    // PUT - ПООЛЬЗОВАТЕЛЬ СТАВИТ ЛАЙК ФИЛЬМУ
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЬ УБИРАЕТ ЛАЙК С ФИЛЬМА
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

}
