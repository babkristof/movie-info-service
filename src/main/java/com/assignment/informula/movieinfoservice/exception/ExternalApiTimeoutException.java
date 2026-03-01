package com.assignment.informula.movieinfoservice.exception;

public class ExternalApiTimeoutException extends RuntimeException {
    public ExternalApiTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
