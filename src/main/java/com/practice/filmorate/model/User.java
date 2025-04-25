package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data   // @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor, @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class User {

    int id;

    @NotBlank
    @Email
    String email;

    @NotBlank
    @Pattern(regexp = "\\S+")
    String login;

    // если имя пустое - используем логин - Service
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    Set<Integer> friends;      // идентификаторы пользователей-друзей

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void setName(String name) {
        this.name = (name == null || name.isBlank()) ? this.login : name.trim(); // тернарный оператор -> установка имени
    }

}
