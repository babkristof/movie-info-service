package com.assignment.informula.movieinfoservice.exception;

public class MovieProviderNotFoundException extends RuntimeException {
    public MovieProviderNotFoundException(String message) {
        super(message);
    }
}
