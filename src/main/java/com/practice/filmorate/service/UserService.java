package com.practice.filmorate.service;

import com.practice.filmorate.exception.FilmNotFoundException;
import com.practice.filmorate.exception.UserNotFoundException;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.UserStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// UserService, который будет отвечать за такие операции с пользователями, как добавление в друзья, удаление из друзей,
// вывод списка общих друзей. Пока пользователям не надо одобрять заявки в друзья — добавляем сразу. То есть если Лена
// стала другом Саши, то это значит, что Саша теперь друг Лены.

@Service
@Slf4j

public class UserService {
    public UserStorage userStorage;

    // КОНСТРУКТОР
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // GET - СПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    // GET - КОНКРЕТНЫЙ ПОЛЬЗОВАТЕЛЬ (ПО id)
    public Optional<User> findById(int id) {
        return userStorage.findById(id);
    }

    // GET - СПИСОК ДРУЗЕЙ (ПО id ПОЛЬЗОВАТЕЛЯ)
    public List<User> findAllFriends(int id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + id + ") не найден"));
        return user.getFriends().stream()
                // переводим айди друзей в объекты друзей
                .map(friendId -> userStorage.findById(friendId)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + friendId + ") не найден")))
                .toList();
    }

    // GET - СПИСОК ОБЩИХ ДРУЗЕЙ С ДРУГИМ ПОЛЬЗОВАТЕЛЕМ
    public List<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        List<User> commonFriends = new ArrayList<>();
        for (int i = 0; i < findAllFriends(id).size(); i++) {
            for (int j = 0; j < findAllFriends(otherId).size(); j++) {
                commonFriends.add(findAllFriends(id).get(i));
            }
        }
        return commonFriends;
    }

    // POST - НОВЫЙ ПОЛЬЗОВАТЕЛЬ
    public User create(User user) {
        return userStorage.create(user);
    }

    // PUT - ОБНОВИТЬ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ
    public User update(User user) {
        return userStorage.update(user);
    }

    // PUT - В СПИСОК ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    public User addNewFriend(int id, int friendId) {
        log.info("Добавление нового друга для пользователя {}: {}", findById(id), findById(friendId));
        User user = userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + id + ") не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + friendId + ") не найден"));
        if (!user.getFriends().add(friendId)) {
            throw new IllegalStateException("Пользователь уже есть в списке друзей");
        }
        return userStorage.update(user);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЯ ПО id
    public void delete(int id) {
        userStorage.delete(id);
    }

    // DELETE - ИЗ СПИСКА ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    public User deleteFriend(int id, int friendId) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + id + ") не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + friendId + ") не найден"));
        if (!user.getFriends().remove(friendId)) {
            throw new IllegalStateException("Пользователя нет в списке друзей");
        }
        return userStorage.update(user);
    }
}
