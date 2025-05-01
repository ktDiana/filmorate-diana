package com.practice.filmorate;

import com.practice.filmorate.exception.UserNotFoundException;
import com.practice.filmorate.model.User;
import com.practice.filmorate.service.UserService;
import com.practice.filmorate.storage.UserStorage;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional

class UserStorageJdbcTest {

    private final UserStorage userStorage;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserStorageJdbcTest(UserStorage userStorage, UserService userService, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setEmail("olya@java.com");
        user.setLogin("olya");
        user.setName("Olya");
        user.setBirthday(LocalDate.of(1999, 9, 29));
        User createdUser = userStorage.create(user);

        assertThat(createdUser.getId()).isGreaterThan(0);
        assertThat(createdUser.getEmail()).isEqualTo("olya@java.com");
        assertThat(createdUser.getLogin()).isEqualTo("olya");
        assertThat(createdUser.getName()).isEqualTo("Olya");
        assertThat(createdUser.getBirthday()).isEqualTo(LocalDate.of(1999, 9, 29));
        assertThat(userService.findFriends(user.getId())).isEmpty();
    }

    @Test
    void testCreateUserWithFriends() {
        User user = new User();
        user.setEmail("sasha@java.com");
        user.setLogin("sasha");
        user.setName("Sasha");
        user.setBirthday(LocalDate.of(1999, 9, 29));
        User createdUser = userStorage.create(user);

        userService.addFriend(user.getId(), 1);
        userService.confirmFriend(1, user.getId());

        // вытащили пользователя из хранилища
        User newUser = userStorage.findById(createdUser.getId())
                // Я полагала, что нужно кидать своё исключение UserNotFoundException, но сказано, что для тестов
                // следует использовать AssertionError
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));
        // убеждаемся, что у него 1 закинутый друг
        Collection<User> friends = userService.findFriends(newUser.getId());
        assertThat(friends).extracting(User::getId).containsExactlyInAnyOrder(1);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("sveta@java.com");
        user.setLogin("sveta");
        user.setName("Svetlana");
        user.setBirthday(LocalDate.of(2000, 10, 12));
        user = userStorage.create(user);

        user.setEmail("newsveta@java.com");
        user.setName("Svetlana UPDATED");
        userService.addFriend(user.getId(), 1);
        userService.confirmFriend(1, user.getId());
        // userService.update(user);
        User updatedUser = userStorage.update(user);

        assertThat(updatedUser.getEmail()).isEqualTo("newsveta@java.com");
        assertThat(updatedUser.getName()).isEqualTo("Svetlana UPDATED");

        Collection<User> friends = userService.findFriends(user.getId());
        assertThat(friends).extracting(User::getId).containsExactlyInAnyOrder(1);

//        User newUser = userStorage.findById(user.getId())
//                .orElseThrow(() -> new AssertionError("Пользователь не найден"));
//        assertThat(newUser.getEmail()).isEqualTo("newsveta@java.com");
//
//        Collection<User> friendsFromDB = userService.findFriends(newUser.getId());
//        assertThat(friendsFromDB).extracting(User::getId).containsExactlyInAnyOrder(1);
    }

    @Test
    void testUpdateUserNotFound() {
        User user = new User();
        user.setId(2025);
        user.setEmail("manas@java.com");
        user.setLogin("manas");
        user.setName("Manas");
        user.setBirthday(LocalDate.of(2001,12,14));

        assertThrows(UserNotFoundException.class, () -> userStorage.update(user));
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setEmail("bekzat@java.com");
        user.setLogin("bekzat");
        user.setName("Bekzat");
        user.setBirthday(LocalDate.of(2001,10,31));
        user = userStorage.create(user);

        userStorage.delete(user.getId());
        Optional<User> deletedUser = userStorage.findById(user.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testFindById() {
        User user = userStorage.findById(1)
                .orElseThrow(() -> new AssertionError("Пользователь с id = 1 не найден"));
        assertThat(user.getEmail()).isEqualTo("diana31@java.com");
        assertThat(user.getLogin()).isEqualTo("diana");
        assertThat(user.getName()).isEqualTo("Diana");
        assertThat(userService.findFriends(user.getId())).isEmpty();
    }

    @Test
    void testFindByIdNotFound() {
        Optional<User> user = userStorage.findById(2002);
        assertThat(user).isEmpty();
    }

    @Test
    void testFindAll() {
        Collection<User> users = userStorage.findAll();

        assertThat(users.size()).isEqualTo(3);
        List<String> emails = new ArrayList<>();
        for (User user : users) {
            emails.add(user.getEmail());
            assertThat(userService.findFriends(user.getId())).isEmpty();
        }

        assertThat(emails).containsExactlyInAnyOrder("diana31@java.com", "islam@jaava.com", "amina@jaaava.com");
    }

    @Test
    void testAddFriend() {
        userService.addFriend(2, 1); // Ислам отправил запрос Диане
        Collection<User> friends = userService.findFriends(2);
        assertThat(friends).isEmpty();

        String sql = "select status from user_friends where user_id = ? and friend_id = ?";
        String status = jdbcTemplate.queryForObject(sql, String.class, 2, 1);
        assertThat(status).isEqualTo("unconfirmed");
    }

    @Test
    void testConfirmFriend() {
        userService.addFriend(2, 1); // Ислам отправил запрос Диане
        userService.confirmFriend(1, 2); // Диана всё же приняла заявку

        User diana = userStorage.findById(1)
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));

        Collection<User> dianaFriends = userService.findFriends(diana.getId());
        // из каждого объекта User в коллекции dianaFriends нужно извлечь значение его id с помощью метода getId()
        // получается коллекция всех id, взятых из объектов User
        assertThat(dianaFriends).hasSize(1);
        assertThat(dianaFriends).extracting(User::getId).containsExactlyInAnyOrder(2);

        User islam = userStorage.findById(2)
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));

        Collection<User> islamFriends = userService.findFriends(islam.getId());
        assertThat(islamFriends).extracting(User::getId).containsExactlyInAnyOrder(1); // Диана есть в списке друзей Ислама
        assertThat(islamFriends).hasSize(1);
    }

    @Test
    void testRemoveFriend() {
        userService.addFriend(2, 1);  // Ислам отправил запрос Диане
        userService.confirmFriend(1, 2);  // Диана приняла заявку Ислама
        userService.deleteFriend(2, 1);  // а потом Ислам её удалил

        Collection<User> friends = userService.findFriends(1);
        assertThat(friends).hasSize(0);
    }

    @Test
    void testFindFriends() {
        userService.addFriend(2, 1); // Ислам отправил запрос Диане
        userService.confirmFriend(1, 2); // Диана приняла заявку Ислама

        userService.addFriend(3, 1); // Амина отправила запрос Диане
        userService.confirmFriend(1, 3); // Диана приняла заявку Амины

        User diana = userStorage.findById(1)
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));

        Collection<User> dianaFriends = userService.findFriends(diana.getId());
        assertThat(dianaFriends).extracting(User::getId).containsExactlyInAnyOrder(2, 3);
        assertThat(dianaFriends).hasSize(2);
    }

    @Test
    void testFindCommonFriends() {
        userService.addFriend(3, 1);  // Амина отправила запрос Диане
        userService.confirmFriend(1, 3); // Диана приняла заявку Амины

        userService.addFriend(3, 2); // Амина отправила запрос Исламу
        userService.confirmFriend(2, 3); // Ислам принял заявку Амины

        Collection<User> commonFriends = userService.findCommonFriends(1, 2);
        assertThat(commonFriends).hasSize(1);

        assertThat(commonFriends).extracting(User::getId).containsExactlyInAnyOrder(3);

        User diana = userStorage.findById(1)
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));
        User islam = userStorage.findById(2)
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));

        Collection<User> dianaFriends = userService.findFriends(diana.getId());
        assertThat(dianaFriends).extracting(User::getId).containsExactlyInAnyOrder(3);

        Collection<User> islamFriends = userService.findFriends(islam.getId());
        assertThat(islamFriends).extracting(User::getId).containsExactlyInAnyOrder(3);
    }

    @Test
    void testCreateUserWithWrongEmail() {
        User user = new User();
        user.setEmail("wrongEmail");
        user.setLogin("something");
        user.setBirthday(LocalDate.of(2025,4,30));
        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void testCreateUserWithBlankLogin() {
        User user = new User();
        user.setEmail("email@java.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1979,7,8));
        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void testCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("email@java.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.now().plusDays(10));
        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void testCreateUserWithEmptyName() {
        User user = new User();
        user.setEmail("newuser@java.com");
        user.setLogin("testlogin");
        user.setName("");
        user.setBirthday(LocalDate.of(1979,6,30));

        User created = userService.create(user);
        assertThat(created.getName()).isEqualTo("testlogin");
        assertThat(userService.findFriends(created.getId())).isEmpty();
    }
}