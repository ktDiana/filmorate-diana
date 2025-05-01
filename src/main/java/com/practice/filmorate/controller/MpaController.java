package com.practice.filmorate.controller;

import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.service.MpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor

public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAllMpa() {
        return mpaService.findAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable int id) {
        return mpaService.findMpaById(id);
    }
}
