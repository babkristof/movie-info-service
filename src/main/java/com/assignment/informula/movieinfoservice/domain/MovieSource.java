package com.assignment.informula.movieinfoservice.domain;

import com.assignment.informula.movieinfoservice.exception.InvalidApiException;

import java.util.Locale;

public enum MovieSource {
    OMDB,
    TMDB;

    public static MovieSource from(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidApiException("Movie source is required. Allowed values: omdb, tmdb");
        }
        try {
            return MovieSource.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidApiException("Unsupported movie source: " + value + ". Allowed values: omdb, tmdb");
        }
    }
}
