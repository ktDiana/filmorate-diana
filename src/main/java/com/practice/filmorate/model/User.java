package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

//@Data   // @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor, @ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class User {

    int id;

    @NotBlank(message = "Поле email не должно быть пустым")
    @Email(message = "Электронная почта должна содержать символ @")
    String email;

    @NotBlank(message = "Поле login не должно быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин пользователя не может содержать пробелы")
    String login;

    // если имя пустое - используем логин - Service
    String name;

    @NotNull(message = "Дата рождения пользователя не должно быть пустым")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    // Убрала это поле, потому что мы полагаемся на данные из БД user_friends
    // я надеюсь(((((((((((((
    // Set<Integer> friends = new HashSet<>();      // идентификаторы пользователей-друзей

    public void setName(String name) {
        this.name = (name == null || name.isBlank()) ? this.login : name.trim(); // тернарный оператор -> установка имени
    }

}
