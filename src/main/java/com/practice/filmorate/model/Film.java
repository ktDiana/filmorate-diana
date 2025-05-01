package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class Film {

    int id;

    @NotBlank(message = "Поле name не должно быть пустым")
    String name;

    @Size(max = 200, message = "Поле description не должно превышать 200 символов")
    String description;

    @NotNull(message = "Дата релиза не должна быть null")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    LocalDate releaseDate;

    @Positive(message = "Duration должна быть положительной")
    int duration;

    Set<Genre> genres = new HashSet<>();      // Список id жанров

    Mpa mpa;

    Set<Integer> likes = new HashSet<>();     // идентификаторы существующих пользователей,  поставивших лайк

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, int mpaId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = new Mpa(mpaId);

    }
}
