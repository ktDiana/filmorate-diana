package com.practice.filmorate.storage;

import com.practice.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// Создайте интерфейсы FilmStorage и UserStorage, в которых будут определены методы добавления, удаления и модификации объектов.

public interface FilmStorage {

    Collection<Film> findAll();

    Optional<Film> findById(int id);

    Film create(Film film);

    Film update(Film film);

    void delete(int id);

}
