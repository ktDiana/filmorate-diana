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

}
