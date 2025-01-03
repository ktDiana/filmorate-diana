package com.practice.filmorate.storage.impl;

import com.practice.filmorate.exception.IncorrectParameterException;
import com.practice.filmorate.exception.UserNotFoundException;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.UserStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

// InMemoryFilmStorage и InMemoryUserStorage, имплементирующие новые интерфейсы, и перенесите туда всю логику хранения,
// обновления и поиска объектов.
// Добавьте к InMemoryFilmStorage и InMemoryUserStorage аннотацию @Component, чтобы впоследствии пользоваться внедрением
// зависимостей и передавать хранилища сервисам.

@Component
@Slf4j

public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int uniqueId = 0;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(int id) {
        for (User user : users.values()) {
            if (user.getId() == id) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        user.setId(getUniqueId());
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        log.info("Создание нового пользователя: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с данным id (" + user.getId() + ") не найден");
        }
        log.info("Обновление данных пользователя: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int id) {
        if (id <= 0 || id >= users.size()) {
            throw new IncorrectParameterException("Пользователь с данным id (" + id + ") не найден");
        }
        log.info("Удаление пользователя: {}", findById(id));
        users.remove(id);
    }

    public int getUniqueId() {
        return uniqueId++;
    }
}
