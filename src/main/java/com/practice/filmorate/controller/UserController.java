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
    @GetMapping("/{id}")
    public User findById(@PathVariable int id) {
        return userService.findById(id);
    }

    // GET - СПИСОК ДРУЗЕЙ (ПО id ПОЛЬЗОВАТЕЛЯ)
    @GetMapping("/{id}/friends")
    public Collection<User> findAllFriends(@PathVariable int id) {
        return userService.findFriends(id);
    }

    // GET - СПИСОК ОБЩИХ ДРУЗЕЙ С ДРУГИМ ПОЛЬЗОВАТЕЛЕМ
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
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
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public void confirmFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.confirmFriend(id, friendId);
    }

    // DELETE - ПОЛЬЗОВАТЕЛЯ ПО id
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    // DELETE - ИЗ СПИСКА ДРУЗЕЙ ПОЛЬЗОВАТЕЛЯ
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

}

