package com.practice.filmorate.storage.impl;

import com.practice.filmorate.model.Genre;
import com.practice.filmorate.storage.GenreStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class GenreStorageImpl implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAllGenres() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    @Override
    public Optional<Genre> findGenreById(int genreId) {
        String sql = "select * from genres where id = ?";
        List<Genre> result = jdbcTemplate.query(sql, this::mapRow, genreId);
        return result.stream().findFirst();
    }

    @Override
    public boolean existsById(int id) {
        String sql = "select count(*) from genres where id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        // count = 1, если объект нашёлся
        return count != null && count > 0;
    }

    private Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
