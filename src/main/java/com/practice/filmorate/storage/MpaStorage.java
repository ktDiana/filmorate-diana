package com.practice.filmorate.storage;

import com.practice.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {

    Collection<Mpa> findAllMpa();

    Optional<Mpa> findMpaById(int mpaId);

    boolean existsById(int id);

}
