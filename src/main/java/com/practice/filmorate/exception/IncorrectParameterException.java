package com.practice.filmorate.exception;

import lombok.Getter;

public class IncorrectParameterException extends RuntimeException {
    private final String parameter;

    public IncorrectParameterException(String parameter) {
        super("Ошибка с полем: " + parameter);
        this.parameter = parameter;
    }
}

