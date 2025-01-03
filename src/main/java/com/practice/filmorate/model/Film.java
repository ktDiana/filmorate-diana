package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Film {

    int id;

    @NotBlank
    String name;

    @Size(max = 200)
    String description;

    @NotNull
    @PastOrPresent
    LocalDate releaseDate;

    @Positive
    int duration;

    Set<Integer> likes = new HashSet<>();     // идентификаторы существующих пользователей

}
