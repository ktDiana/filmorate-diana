package com.practice.filmorate.controller;

import com.practice.filmorate.model.User;
import com.practice.filmorate.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // КОНСТРУКТОР
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET - СПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ
    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    // GET - КОНКРЕТНЫЙ ПОЛЬЗОВАТЕЛЬ (ПО id)
    public Optional<User> findById(int id) {
        return userService.findById(id);
    }

    // GET - СПИСОК ДРУЗЕЙ (ПО id ПОЛЬЗОВАТЕЛЯ)
    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable int id) {
        return userService.findAllFriends(id);
    }

    // GET - СПИСОК ОБЩИХ ДРУЗЕЙ С ДРУГИМ ПОЛЬЗОВАТЕЛЕМ
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    // POST - НОВЫЙ ПОЛЬЗОВАТЕЛЬ
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    // PUT - ОБНОВИТЬ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    // PUT - В СПИСОК ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    @PutMapping("/{id}/friends/{friendId}")
    public User addNewFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addNewFriend(id, friendId);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЯ ПО id
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    // DELETE - ИЗ СПИСКА ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

}

