package com.practice.filmorate.service;

import com.practice.filmorate.exception.GenreNotFoundException;
import com.practice.filmorate.model.Genre;
import com.practice.filmorate.storage.GenreStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class GenreService {

    private final GenreStorage genreStorageImpl;

    public Collection<Genre> findAllGenres() {
        return genreStorageImpl.findAllGenres();
    }

    public Genre findGenreById(int genreId) {
        return genreStorageImpl.findGenreById(genreId)
                .orElseThrow(() -> new GenreNotFoundException("Жанр с id = " + genreId + " не найден"));
    }
}

