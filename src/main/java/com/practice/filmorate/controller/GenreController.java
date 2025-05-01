package com.practice.filmorate.controller;

import com.practice.filmorate.model.Genre;
import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor

public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAllGenres() {
        return genreService.findAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable int id) {
        return genreService.findGenreById(id);
    }
}