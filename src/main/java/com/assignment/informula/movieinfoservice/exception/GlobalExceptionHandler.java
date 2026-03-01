package com.assignment.informula.movieinfoservice.exception;

import com.assignment.informula.movieinfoservice.controller.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidApiException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidApi(RuntimeException ex) {
        log.warn("Invalid api param: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(error("INVALID_API", ex.getMessage(), null));
    }

    @ExceptionHandler(MovieProviderNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProviderNotFound(MovieProviderNotFoundException ex) {
        return ResponseEntity.badRequest().body(error("INVALID_API", ex.getMessage(), null));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(HandlerMethodValidationException ex) {
        String message = ex.getAllErrors().isEmpty()
                ? "Validation failed"
                : ex.getAllErrors().getFirst().getDefaultMessage();

        return ResponseEntity.badRequest().body(error("VALIDATION_ERROR", message, null));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Validation failed");

        return ResponseEntity.badRequest().body(error("VALIDATION_ERROR", message, null));
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage();
        return ResponseEntity.badRequest().body(error("INVALID_REQUEST", message, null));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingRequestParameter(MissingServletRequestParameterException ex) {
        String message = "Missing required request parameter: " + ex.getParameterName();
        return ResponseEntity.badRequest().body(error("MISSING_PARAMETER", message, null));
    }

    @ExceptionHandler(ExternalApiTimeoutException.class)
    public ResponseEntity<ErrorResponseDto> handleTimeout(ExternalApiTimeoutException ex) {
        log.warn("External API timeout: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(error("EXTERNAL_API_TIMEOUT", "External API request timed out", ex.getMessage()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponseDto> handleExternalError(ExternalApiException ex) {
        log.warn("External API failure: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(error("EXTERNAL_API_FAILURE", "External API request failed", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        log.error("Unhandled server error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("INTERNAL_SERVER_ERROR", "Unexpected server error", ex.getMessage()));
    }

    private ErrorResponseDto error(String code, String message, String details) {
        return new ErrorResponseDto(new ErrorResponseDto.ErrorBody(code, message, details));
    }
}
