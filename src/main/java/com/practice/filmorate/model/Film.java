package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class Film {

    int id;

    @NotBlank
    String name;

    @Size(max = 200)
    String description;

//    @NotNull
//    @PastOrPresent
    // Аннотации не нужны, поскольку в FilmDbStorage прописана проверка на условие
    LocalDate releaseDate;

    @Positive
    int duration;

    Mpa mpa;

    Set<Genre> genres;

    Set<Integer> likes;     // идентификаторы существующих пользователей
}
