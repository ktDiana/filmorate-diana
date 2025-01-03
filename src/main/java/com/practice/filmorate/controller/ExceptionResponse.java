package com.practice.filmorate.controller;

public class ExceptionResponse {
    private final String exception;

    public ExceptionResponse(String error) {
        this.exception = error;
    }

    public String getError() {
        return exception;
    }
}
