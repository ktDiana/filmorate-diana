package com.practice.filmorate.storage.impl;

import com.practice.filmorate.exception.UserNotFoundException;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary

public class UserStorageJdbcImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserStorageJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "select * from users where id = ?";
        List<User> users = jdbcTemplate.query(sql, this::mapRow, id);
        return users.stream().findFirst();
    }

    @Override
    public boolean existsById(int id) {
        String sql = "select count(*) from users where id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        // count = 1, если объект нашёлся
        return count != null && count > 0;
    }

    @Override
    public User create(User user) {
        // условие с именем
        String name = (user.getName() == null || user.getName().isBlank()) ? user.getLogin() : user.getName();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> map = Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", name,
                "birthday", user.getBirthday()
        );
        int id = simpleJdbcInsert.executeAndReturnKey(map).intValue();
        user.setId(id);

//        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
//            String friendSql = "insert into user_friends (user_id, friend_id, status) values (?, ?, 'confirmed')";
//            for (Integer friendId : user.getFriends()) {
//                jdbcTemplate.update(friendSql, id, friendId);
//            }
//        }
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        int updatedUser = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (updatedUser == 0) {
            throw new UserNotFoundException("Пользователь с данным id не найден: " + user.getId());
        }
//        jdbcTemplate.update("delete from user_friends where user_id = ? and status = 'confirmed'", user.getId());
//        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
//            String friendSql = "insert into user_friends (user_id, friend_id, status) values (?, ?, 'confirmed')";
//            for (Integer friendId : user.getFriends()) {
//                jdbcTemplate.update(friendSql, user.getId(), friendId);
//            }
//        }
        return user;
    }

    @Override
    public void delete(int id) {
        String sql = "delete from users where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new UserNotFoundException("Нельзя добавить в друзья самого себя");
        }
        if (!existsById(friendId)) {
            throw new UserNotFoundException("Пользователь с данным id не найден:" + friendId);
        }
        String sql = "select count(*) from user_friends where user_id = ? and friend_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        if (count == 0) {
            // создаём заявку user -> friend = статус пока 'unconfirmed'
            String requestSql = "insert into user_friends (user_id, friend_id, status) values (?, ?, 'unconfirmed')";
            jdbcTemplate.update(requestSql, userId, friendId);
        }
    }

    @Override
    public void confirmFriend(int userId, int friendId) {
        // подтверждение дружбы, т.е. принимаем заявку
        // это понятнее, если нарисовать на листочке
        // проверяем существование friendId (user_id) -> userId (friend_id)
        String updateSql = "update user_friends set status = 'confirmed' where user_id = ? and friend_id = ? and status = 'unconfirmed'";
        int updated = jdbcTemplate.update(updateSql, friendId, userId);
        // Если запрос успешно изменил хотя бы одну строку, update() вернёт количество затронутых строк, в данном случае это 1
        // если не затронул ни одной строки, то 0
        if (updated == 0) {
            throw new UserNotFoundException("Запрос в друзья от " + friendId + " к " + userId + " не найден или уже подтверждён");
        }
        // если всё ок, то теперь создаём взаимность (подтверждение) userId -> friendId + 'confirmed'
        // существует ли запись в БД, где пользователь user_id добавил в друзья пользователя friend_id
        String sqlCheck = "select count(*) from user_friends where user_id = ? and friend_id = ?";
        // Spring позволяет запрашивать примитивы (int вместо Integer). Это автоматически заменяет null на значение по умолчанию для примитива (т.е. 0 для int)
        int reciprocity = jdbcTemplate.queryForObject(sqlCheck, int.class, userId, friendId);
        if (reciprocity == 0) {
            // если взаимности нет, то добавляем
            String insertSql = "insert into user_friends (user_id, friend_id, status) values (?, ?, 'confirmed')";
            jdbcTemplate.update(insertSql, userId, friendId);
        } else {
            // если взаимность есть, но там неподтвержденный статус (мало ли), то теперь его подтверждаем, ведь ВЗАИМНО
            String updateBack = "update user_friends set status = 'confirmed' where user_id = ? and friend_id = ?";
            jdbcTemplate.update(updateBack, userId, friendId);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        // user -> friend удалили, надо удалить так же и friend -> user, полагаю
        jdbcTemplate.update("delete from user_friends where user_id = ? and friend_id = ?", userId, friendId);
        jdbcTemplate.update("delete from user_friends where user_id = ? and friend_id = ?", friendId, userId);
    }

    @Override
    public Collection<User> findFriends(int userId) {
        String sql = "select users.* " +
                "from users " +
                "join user_friends on users.id = user_friends.friend_id " +
                "where user_friends.user_id = ? and user_friends.status = 'confirmed'";
        return jdbcTemplate.query(sql, this::mapRow, userId);
    }

    @Override
    public Collection<User> findCommonFriends(int userId, int friendId) {
        String sql = "select users.* " +
                "from users " +
                "join user_friends friends1 on users.id = friends1.friend_id " +
                "join user_friends friends2 on users.id = friends2.friend_id " +
                "where friends1.user_id = ? and friends2.user_id = ? " +
                "and friends1.status = 'confirmed' and friends2.status = 'confirmed'";
        return jdbcTemplate.query(sql, this::mapRow, userId, friendId);
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

}
