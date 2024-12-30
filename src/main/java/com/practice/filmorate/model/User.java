package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data   // @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor, @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class User {

    @Getter
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

    Set<Integer> friends;      // идентификаторы пользователей-друзей

    public String getName() {
        return (name == null || name.isEmpty()) ? login : name.trim();     // тернарный оператор -> установка имени
    }

}
