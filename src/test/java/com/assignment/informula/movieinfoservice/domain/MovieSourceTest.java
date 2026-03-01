package com.assignment.informula.movieinfoservice.domain;

import com.assignment.informula.movieinfoservice.exception.InvalidApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovieSourceTest {

    @Test
    void fromReturnsEnumForSupportedLowercaseValue() {
        assertEquals(MovieSource.OMDB, MovieSource.from("omdb"));
    }

    @Test
    void fromReturnsEnumForTrimmedMixedCaseValue() {
        assertEquals(MovieSource.TMDB, MovieSource.from("  TmDb "));
    }

    @Test
    void fromThrowsWhenValueIsNull() {
        InvalidApiException ex = assertThrows(InvalidApiException.class, () -> MovieSource.from(null));

        assertEquals("Movie source is required. Allowed values: omdb, tmdb", ex.getMessage());
    }

    @Test
    void fromThrowsWhenValueIsBlank() {
        InvalidApiException ex = assertThrows(InvalidApiException.class, () -> MovieSource.from("   "));

        assertEquals("Movie source is required. Allowed values: omdb, tmdb", ex.getMessage());
    }

    @Test
    void fromThrowsWhenValueIsUnsupported() {
        InvalidApiException ex = assertThrows(InvalidApiException.class, () -> MovieSource.from("imdb"));

        assertEquals("Unsupported movie source: imdb. Allowed values: omdb, tmdb", ex.getMessage());
    }
}
