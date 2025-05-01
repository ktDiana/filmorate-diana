package com.practice.filmorate.storage.impl;

import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.storage.MpaStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class MpaStorageImpl implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> findAllMpa() {
        String sql = "select * from mpa order by id";
        // не создаем отдельный mapRow(), а сразу реализация функционального интерфейса через лямбда-выражение
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Mpa(rs.getInt("id"),
                                                     rs.getString("name")));
    }

    public Optional<Mpa> findMpaById(int mpaId) {
        String sql = "select * from mpa where id = ?";
        List<Mpa> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Mpa(rs.getInt("id"),
                                                     rs.getString("name")),
                mpaId);
        return result.stream().findFirst();
    }

    @Override
    public boolean existsById(int id) {
        String sql = "select count(*) from mpa where id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        // count = 1, если объект нашёлся
        return count != null && count > 0;
    }

}
