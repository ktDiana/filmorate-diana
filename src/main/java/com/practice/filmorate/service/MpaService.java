package com.practice.filmorate.service;

import com.practice.filmorate.exception.MpaNotFoundException;
import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.storage.MpaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class MpaService {

    private final MpaStorage mpaStorageImpl;

    public Collection<Mpa> findAllMpa() {
        return mpaStorageImpl.findAllMpa();
    }

    public Mpa findMpaById(int mpaId) {
        return mpaStorageImpl.findMpaById(mpaId)
                .orElseThrow(() -> new MpaNotFoundException("MPA с id = " + mpaId + " не найден"));
    }
}
