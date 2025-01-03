package com.practice.filmorate.exception;

public class InvalidReleaseDateException extends RuntimeException {

    public InvalidReleaseDateException(String message) {
        super(message);
    }
}
