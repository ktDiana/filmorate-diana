package com.practice.filmorate.storage;

import com.practice.filmorate.model.Film;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Optional<Film> findById(int id);

    List<Film> findPopular(int count);

    boolean existsById(int id);

    Film create(Film film);

    Film update(Film film);

    void deleteFilm(int id);

}