package com.practice.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Data   // @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor, @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j

public class User {

    int id;

    @NotBlank
    @Email
    String email;

    @NotBlank
    @Pattern(regexp = "\\S+")
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    public String getName() {
        return (name == null || name.isEmpty()) ? login : name.trim();     // тернарный оператор -> установка имени
    }


}
