package com.smartMunicipal.Smart.Municipal.Services.Exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

