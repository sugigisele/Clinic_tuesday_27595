package com.example.clinicsystem.exception;

// Thrown when a request conflicts with the current data (HTTP 409)
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
