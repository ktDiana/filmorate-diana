package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data   // @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor, @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)

public class User {

    int id;

    @NotBlank
    @Email
    String email;

    @NotBlank
    @Pattern(regexp = "\\S+")
    String login;

    // если имя пустое - используем логин
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    Set<Integer> friends = new HashSet<>();      // идентификаторы пользователей-друзей

    public String setName() {
        return (name == null || name.isEmpty()) ? login : name.trim();     // тернарный оператор -> установка имени
    }

}
