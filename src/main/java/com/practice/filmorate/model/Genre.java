package com.practice.filmorate.model;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class Genre {

    int id;
    String name;

    public Genre(int id) {
        this.id = id;
    }
}