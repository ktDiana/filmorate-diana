package com.practice.filmorate.storage;

import com.practice.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

// Создайте интерфейсы FilmStorage и UserStorage, в которых будут определены методы добавления, удаления и модификации объектов.

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findById(int id);

    boolean existsById(int id);

    User create(User user);

    User update(User user);

    void delete(int id);

    void addFriend(int userId, int friendId);

    void confirmFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<User> findFriends(int userId);

    Collection<User> findCommonFriends(int userId, int friendId);
}
