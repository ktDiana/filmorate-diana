package com.practice.filmorate.storage;

import com.practice.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// Создайте интерфейсы FilmStorage и UserStorage, в которых будут определены методы добавления, удаления и модификации объектов.

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    User create(User user);

    User update(User user);

    void delete(int id);

    void delete(String email);
}
