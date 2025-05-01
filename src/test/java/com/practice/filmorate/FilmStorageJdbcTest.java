package com.practice.filmorate;

import com.practice.filmorate.exception.FilmNotFoundException;
import com.practice.filmorate.exception.InvalidReleaseDateException;
import com.practice.filmorate.model.Genre;
import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.service.FilmService;
import com.practice.filmorate.storage.FilmStorage;
import com.practice.filmorate.model.Film;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional

class FilmStorageJdbcTest {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmStorageJdbcTest(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @Test
    void testFindAllFilms() {
        Collection<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(2);
    }

    @Test
    void testFindFilmById() {
        Film film = filmStorage.findById(1)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id = 1 не найден"));
        assertThat(film.getName()).isEqualTo("Побег из Шоушенка");
        assertThat(film.getMpa().getId()).isEqualTo(4);
        assertThat(film.getGenres()).extracting(Genre::getId).containsExactly(2);
        assertThat(film.getLikes()).isEmpty();
    }

    @Test
    void testFindFilmByIdNotFound() {
        Optional<Film> film = filmStorage.findById(999);
        assertThat(film).isEmpty();
    }

//    @Test
//    void testCreateFilm() {
//        Film film = new Film(0, "Тестовый фильм", "Тестовое описание", LocalDate.of(2025, 4, 1), 123, 1);
//        Film createdFilm = filmStorage.create(film);
//        Optional<Film> currentFilm = filmStorage.findById(createdFilm.getId());
//        assertThat(currentFilm).isPresent();
//        assertThat(currentFilm.get().getName()).isEqualTo("Тестовый фильм");
//    }

    @Test
    void testCreateFilm() {
        Film testFilm = new Film();
        testFilm.setName("Тестовый фильм");
        testFilm.setDescription("Тестовое описание");
        testFilm.setReleaseDate(LocalDate.of(2025, 4, 20));
        testFilm.setDuration(127);
        testFilm.setMpa(new Mpa(1));
        testFilm.setGenres(Set.of(new Genre(1)));
        testFilm.setLikes(new HashSet<>());
        Film created = filmStorage.create(testFilm);

        Film newFilm = filmStorage.findById(created.getId())
                .orElseThrow(() -> new FilmNotFoundException("Тестовый фильм не найден по id"));
        assertThat(newFilm.getName()).isEqualTo("Тестовый фильм");
        assertThat(newFilm.getMpa().getId()).isEqualTo(1);
        assertThat(newFilm.getLikes()).isEmpty();
        Set<Genre> genres = newFilm.getGenres();
        List<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .toList();
        assertEquals(List.of(1), genreIds);
    }

//    @Test
//    void testUpdateFilm() {
//        Film newFilm = filmStorage.create(
//                new Film(0, "Тестовый фильм", "Тестовое описание", LocalDate.of(2025, 4, 20), 190, 4)
//        );
//
//        Film updated = new Film(
//                newFilm.getId(),
//                "Тестовый фильм ОБНОВЛЁННЫЙ",
//                "Тестовое описание ОБНОВЛЁННОЕ",
//                newFilm.getReleaseDate(),
//                newFilm.getDuration(),
//                newFilm.getMpaId()
//        );
//
//        Film result = filmStorage.update(updated);
//        assertThat(result.getName()).isEqualTo("Тестовый фильм ОБНОВЛЁННЫЙ");
//    }

    @Test
    void testUpdateFilm() {
        Film testFilm = new Film();
        testFilm.setId(1);
        testFilm.setName("Тестовый фильм ОБНОВЛЁННЫЙ");
        testFilm.setDescription("Тестовое описание ОБНОВЛЁННОЕ");
        testFilm.setReleaseDate(LocalDate.of(2002, 1, 31));
        testFilm.setDuration(161);
        testFilm.setMpa(new Mpa(4));
        testFilm.setGenres(Set.of(new Genre(3)));
        testFilm.setLikes(new HashSet<>());

        Film updatedFilm = filmStorage.update(testFilm);
        assertThat(updatedFilm.getName()).isEqualTo("Тестовый фильм ОБНОВЛЁННЫЙ");
        assertThat(updatedFilm.getDescription()).isEqualTo("Тестовое описание ОБНОВЛЁННОЕ");
        assertThat(updatedFilm.getMpa().getId()).isEqualTo(4);

        Film currentFilm = filmStorage.findById(1)
                .orElseThrow(() -> new FilmNotFoundException("Тестовый фильм ОБНОВЛЁННЫЙ не найден по id"));
        assertThat(currentFilm.getName()).isEqualTo("Тестовый фильм ОБНОВЛЁННЫЙ");
        assertThat(currentFilm.getMpa().getId()).isEqualTo(4);
        Set<Genre> genres = currentFilm.getGenres();
        List<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .toList();
        assertEquals(List.of(3), genreIds);
    }

    @Test
    void testUpdateFilmThrowsNotFound() {
        Film testFilm = new Film();
        testFilm.setId(999);
        testFilm.setName("Тестовый несуществующий фильм");
        testFilm.setDescription("Тестовое описание");
        testFilm.setReleaseDate(LocalDate.of(2025, 4, 30));
        testFilm.setDuration(123);
        testFilm.setMpa(new Mpa(4));
        testFilm.setGenres(new HashSet<>());
        testFilm.setLikes(new HashSet<>());
        assertThrows(FilmNotFoundException.class, () -> filmStorage.update(testFilm));
    }

    @Test
    void testDeleteFilm() {
        filmStorage.deleteFilm(1);
        Optional<Film> deleted = filmStorage.findById(1);
        assertThat(deleted).isEmpty();
    }

    @Test
    void testFindPopular() {
        filmService.addLike(1, 1);
        filmService.addLike(1, 2);
        filmService.addLike(2, 3);

        List<Film> popular = filmStorage.findPopular(1);
        assertThat(popular).hasSize(1);
        // Идея предлагает использовать .getFirst() вместо .get(0), но "Метод getFirst() доступен только для структур
        // данных, реализующих интерфейс Deque или LinkedList"
        assertThat(popular.get(0).getId()).isEqualTo(1);
        assertThat(popular.get(0).getLikes()).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void testCreateFilmWithBlankName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2002, 1, 31));
        film.setDuration(161);
        film.setMpa(new Mpa(3));
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        assertThrows(ValidationException.class, () -> filmService.create(film));
    }

    @Test
    void testCreateFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Супер-пупер-длинный-тестовый-текст-который-можно-было-расписать-по-другому-но-я-решила-скреативить-там-где-это-точно-допустимо-и-ничего-не-сломает-спасибо-так-что-хочу-передать-привет-всем-недоделанным-проектам-и-предстоящим-проектам-тоже-этот-момент-влияет-на-всё-моё-будущее-надеюсь-что-положительно-буйыртса-без-комментариев");
        film.setReleaseDate(LocalDate.of(2002, 1, 31));
        film.setDuration(161);
        film.setMpa(new Mpa(4));
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        assertThrows(ValidationException.class, () -> filmService.create(film));
    }

    @Test
    void testCreateFilmWithInvalidReleaseDate() {
        // не раньше 28 декабря 1895
        Film film = new Film();
        film.setName("Тестовый фильмчик");
        film.setDescription("Тестовый текстик");
        film.setReleaseDate(LocalDate.of(1895, 12, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1));;
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        assertThrows(InvalidReleaseDateException.class, () -> filmService.create(film));
    }

    @Test
    void testCreateFilmWithWrongDuration() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описаньице");
        film.setReleaseDate(LocalDate.of(2002, 1, 31));
        film.setDuration(-161);
        film.setMpa(new Mpa(2));
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        assertThrows(ValidationException.class, () -> filmService.create(film));
    }

    @Test
    void testCreateValidFilm() {
        Film film = new Film();
        film.setName("Тестовый фильмец");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2002, 1, 1));
        film.setDuration(161);
        film.setMpa(new Mpa(5));
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        Film createdFilm = filmService.create(film);
        assertNotNull(createdFilm);
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getMpa().getId(), createdFilm.getMpa().getId());
        assertThat(createdFilm.getGenres()).isEmpty();
        assertThat(createdFilm.getLikes()).isEmpty();
    }
}