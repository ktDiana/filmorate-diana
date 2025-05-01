package com.practice.filmorate.storage.impl;

import com.practice.filmorate.exception.FilmNotFoundException;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.model.Genre;
import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.storage.FilmStorage;
import com.practice.filmorate.storage.MpaStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary

public class FilmStorageJdbcImpl implements FilmStorage {

    public final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmStorageJdbcImpl(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "select * from films where id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRow, id);
        return films.stream().findFirst();
    }

    public List<Film> findPopular(int count) {
        String sql = """
        select
                films.id,
                films.name,
                films.description,
                films.release_date,
                films.duration,
                films.mpa_id,
                COUNT(film_likes.user_id) as like_count
        from films
        left join film_likes on films.id = film_likes.film_id
        group by films.id, films.name, films.description, films.release_date, films.duration, films.mpa_id
        order by like_count desc
        LIMIT ?
        """;
        return jdbcTemplate.query(sql, this::mapRow, count);
    }

    @Override
    public boolean existsById(int id) {
        String sql = "select count(*) from films where id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        // count = 1, если объект нашёлся
        return count != null && count > 0;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> map = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa_id", film.getMpa() != null ? film.getMpa().getId() : null
        );
        int id = simpleJdbcInsert.executeAndReturnKey(map).intValue();

        film.setId(id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "insert into film_genres (film_id, genre_id) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSql, id, genre.getId());
            }
        }

        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            String likeSql = "insert into film_likes (film_id, user_id) values (?, ?)";
            for (Integer userId : film.getLikes()) {
                jdbcTemplate.update(likeSql, id, userId);
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? where id = ?";
        int updatedFilm = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (updatedFilm == 0) {
            throw new FilmNotFoundException("Фильм с данным id не найден: " + film.getId());
        }

        // удаляем из таблицы фильм+жанр
        jdbcTemplate.update("delete from film_genres where film_id = ?", film.getId());

        // возвращаем в таблицу фильм+жанр обновлённый вариант
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "insert into film_genres (film_id, genre_id) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            }
        }

        // удаляем из таблицы фильм+лайки
        jdbcTemplate.update("delete from film_likes WHERE film_id = ?", film.getId());

        // возвращаем в таблицу фильм+лайки обновлённый вариант
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            String likeSql = "insert into film_likes (film_id, user_id) values (?, ?)";
            for (Integer userId : film.getLikes()) {
                jdbcTemplate.update(likeSql, film.getId(), userId);
            }
        }

        return film;
    }

    @Override
    public void deleteFilm(int id) {
        String sql = "delete from films where id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // загрузка полного объекта MPA с именем
        int mpaId = rs.getInt("mpa_id");
        Optional<Mpa> resultMpa = mpaStorage.findMpaById(mpaId);
        film.setMpa(resultMpa.orElse(null));

        // вытаскиваем из таблицы фильм+жанр жанры переданного фильма

        // жанры: выбираем уникальные и отсортированные по genre_id
       String sql = "select genres.id as genre_id, genres.name as genre_name " +
                "from film_genres " +
                "join genres on film_genres.genre_id = genres.id " +
                "where film_id = ? " +
                "group by genres.id, genres.name " +
                "order by genres.id";
        List<Genre> genres = jdbcTemplate.query(
                sql,
                (rsGenre, row) -> new Genre(rsGenre.getInt("id"),
                                                         rsGenre.getString("name")),
                film.getId());
        film.setGenres(new LinkedHashSet<>(genres));

        // загрузка лайков
        String sqlLikes = "select user_id from film_likes " +
                "where film_id = ? " +
                "order by user_id";
        List<Integer> likes = jdbcTemplate.query(
                sqlLikes,
                (rsLikes, row) -> rsLikes.getInt("user_id"),
                film.getId());
        film.setLikes(new HashSet<>(likes));

        return film;
    }
}