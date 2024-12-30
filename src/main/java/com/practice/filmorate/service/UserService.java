package com.practice.filmorate.service;

import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

// UserService, который будет отвечать за такие операции с пользователями, как добавление в друзья, удаление из друзей,
// вывод списка общих друзей. Пока пользователям не надо одобрять заявки в друзья — добавляем сразу. То есть если Лена
// стала другом Саши, то это значит, что Саша теперь друг Лены.

@Service
public class UserService {
    public UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // GET - СПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    // GET - СПИСОК ДРУЗЕЙ (ПО id ПОЛЬЗОВАТЕЛЯ)
    public List<User> findAllFriends(int id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с данным id (" + id + ") не найден"));

        return user.getFriends().stream()
                .map(friendId -> userStorage.findById(friendId)
                        .orElseThrow(() -> new IllegalArgumentException("Пользователь с данным id (" + friendId + ") не найден")))
                .toList();
    }

    // GET - СПИСОК ОБЩИХ ДРУЗЕЙ С ДРУГИМ ПОЛЬЗОВАТЕЛЕМ
    public List<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return null;
    }

    // POST - НОВЫЙ ПОЛЬЗОВАТЕЛЬ
    public User create(User user) {
        return userStorage.create(user);
    }

    // PUT - ОБНОВИТЬ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ
    public User update(User user) {
        return userStorage.create(user);
    }

    // PUT - В СПИСОК ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    public User addNewFriend(int id, int friendId) {
        return null;
    }

    // DELETE - ПОЛЬЗОВАТЕЛЯ ПО id
    public void delete(int id) {
        userStorage.delete(id);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЯ ПО email
    public void delete(User user) {
        userStorage.delete(user.getEmail());
    }

    // DELETE - ИЗ СПИСКА ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    public void deleteFriend(int id, int friendId) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с данным id (" + id + ") не найден"));
        user.getFriends().remove(friendId);
    }
}
