package com.practice.filmorate.service;

import com.practice.filmorate.exception.UserNotFoundException;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.UserStorage;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

// UserService, который будет отвечать за такие операции с пользователями, как добавление в друзья, удаление из друзей,
// вывод списка общих друзей. Пока пользователям не надо одобрять заявки в друзья — добавляем сразу. То есть если Лена
// стала другом Саши, то это значит, что Саша теперь друг Лены.

@Service
@Slf4j

public class UserService {

    public final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // GET - СПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    // GET - КОНКРЕТНЫЙ ПОЛЬЗОВАТЕЛЬ (ПО id)
    public User findById(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + id + ") не найден"));
    }

    // GET - СПИСОК ДРУЗЕЙ (ПО id ПОЛЬЗОВАТЕЛЯ)
    public Collection<User> findFriends(int userId) {
        findById(userId);
        return userStorage.findFriends(userId);
    }

    // GET - СПИСОК ОБЩИХ ДРУЗЕЙ С ДРУГИМ ПОЛЬЗОВАТЕЛЕМ
    public Collection<User> findCommonFriends(int userId, int friendId) {
        findById(userId);
        findById(friendId);
        return userStorage.findCommonFriends(userId, friendId);
    }

    // POST - НОВЫЙ ПОЛЬЗОВАТЕЛЬ
    public User create(User user) {
        validate(user);
        log.info("Создан новый пользователь: {}", user.getName());
        return userStorage.create(user);
    }

    // PUT - ОБНОВИТЬ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ
    public User update(User user) {
        validate(user);
        log.info("Обновлён пользователь: {}", user.getName());
        return userStorage.update(user);
    }

    // ПОДТВЕРЖДЕНИЕ ДРУЖБЫ
    public void confirmFriend(int userId, int friendId) {
        findById(userId);
        findById(friendId);
        userStorage.confirmFriend(userId, friendId);
        log.info("Пользователь {} подтвердил дружбу с {}", userId, friendId);
    }

    // PUT - В СПИСОК ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    public void addFriend(int userId, int friendId) {
        log.info("Добавление нового друга {} для пользователя {}", friendId, userId);
        findById(userId);
        findById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЯ ПО id
    public void delete(int id) {
        userStorage.delete(id);
        log.info("Удалён пользователь: {}", id);
    }

    // DELETE - ИЗ СПИСКА ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    public void deleteFriend(int userId, int friendId) {
        findById(userId);
        findById(friendId);
        userStorage.removeFriend(userId, friendId);
        log.info("Удалён друг {} у пользователя {}", friendId, userId);
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный login");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
