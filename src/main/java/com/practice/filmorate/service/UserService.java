package com.practice.filmorate.service;

import com.practice.filmorate.exception.FilmNotFoundException;
import com.practice.filmorate.exception.UserNotFoundException;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// UserService, который будет отвечать за такие операции с пользователями, как добавление в друзья, удаление из друзей,
// вывод списка общих друзей. Пока пользователям не надо одобрять заявки в друзья — добавляем сразу. То есть если Лена
// стала другом Саши, то это значит, что Саша теперь друг Лены.

@Service
@Slf4j
@RequiredArgsConstructor

public class UserService {

    public final UserStorage userStorage;

    // GET - СПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    // GET - КОНКРЕТНЫЙ ПОЛЬЗОВАТЕЛЬ (ПО id)
    public User findById(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Пользователь с данным id (" + id + ") не найден"));
    }

    // GET - СПИСОК ДРУЗЕЙ (ПО id ПОЛЬЗОВАТЕЛЯ)
    public List<User> findAllFriends(int id) {
        User user = findById(id);
        return user.getFriends().stream()
                // переводим айди друзей в объекты друзей
                .map(friendId -> userStorage.findById(friendId)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id (" + friendId + ") не найден")))
                .toList();
    }

    // GET - СПИСОК ОБЩИХ ДРУЗЕЙ С ДРУГИМ ПОЛЬЗОВАТЕЛЕМ
    public Set<User> findCommonFriends(int id, int otherId) {
        Set<User> friendsOfUser1 = new HashSet<>(findAllFriends(id));
        Set<User> friendsOfUser2 = new HashSet<>(findAllFriends(otherId));
        friendsOfUser1.retainAll(friendsOfUser2);
        return friendsOfUser1; // Возвращаем список общих друзей
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
        log.info("Взаимное добавление нового друга для пользователя {}: {}", id, friendId);
        User user = findById(id);
        User friend = findById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id); // Взаимная дружба
        userStorage.update(user);
        userStorage.update(friend); // Обновляем и друга
        return user;
    }

    // DELETE - ПОЛЬЗОВАТЕЛЯ ПО id
    public void delete(int id) {
        userStorage.delete(id);
    }

    // DELETE - ИЗ СПИСКА ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    public User deleteFriend(int id, int friendId) {
        User currentUser = findById(id);
        User friendUser = findById(friendId);
        currentUser.getFriends().remove(friendId);
        friendUser.getFriends().remove(id);
        return userStorage.update(currentUser);
    }
}
