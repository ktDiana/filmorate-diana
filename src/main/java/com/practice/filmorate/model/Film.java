package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j

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

    public void setReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            String message = "Дата релиза не должна быть раньше 28 декабря 1895 года";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        this.releaseDate = releaseDate;
    }
}
