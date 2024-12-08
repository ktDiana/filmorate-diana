package com.practice.filmorate;

import com.practice.filmorate.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }
//
//    User user = User.builder()
//            .email("user1@exmpl.ru")
//            .login("user1")
//            .name("User1")
//            .birthday(LocalDate.of(2000,1,1))
//            .build();
//
//    User user2 = User.builder()
//            .email("user2@exmpl.ru")
//            .login("user2")
//            .name("User2")
//            .birthday(LocalDate.of(2001,1,1))
//            .build();

}
