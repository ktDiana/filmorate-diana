package com.practice.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

//@Data
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class Mpa {

    int id;
    String name;

    public Mpa(int id) {
        this.id = id;
    }
}