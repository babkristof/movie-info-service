package com.assignment.informula.movieinfoservice.service;

import com.assignment.informula.movieinfoservice.domain.MovieProvider;
import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import com.assignment.informula.movieinfoservice.exception.MovieProviderNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovieProviderRegistryTest {

    @Test
    void resolveReturnsRegisteredProvider() {
        MovieProvider omdbProvider = new StubMovieProvider(MovieSource.OMDB);
        MovieProviderRegistry registry = new MovieProviderRegistry(List.of(omdbProvider));

        assertSame(omdbProvider, registry.resolve(MovieSource.OMDB));
    }

    @Test
    void resolveThrowsWhenProviderIsMissing() {
        MovieProviderRegistry registry = new MovieProviderRegistry(List.of(new StubMovieProvider(MovieSource.OMDB)));

        assertThrows(MovieProviderNotFoundException.class, () -> registry.resolve(MovieSource.TMDB));
    }

    private record StubMovieProvider(MovieSource supports) implements MovieProvider {
        @Override
        public List<MovieSummary> search(String title) {
            return List.of();
        }
    }
}
