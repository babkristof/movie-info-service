package com.assignment.informula.movieinfoservice.service;

import java.util.Locale;

public final class MovieTitleNormalizer {
    private MovieTitleNormalizer() {
    }

    public static String normalize(String movieTitle) {
        if (movieTitle == null) {
            return "";
        }
        return movieTitle.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
