package com.assignment.informula.movieinfoservice.service;

import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import com.assignment.informula.movieinfoservice.event.MovieSearchLogEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovieSearchServiceImplTest {

    @Test
    void searchMoviesReturnsCachedResultsAndPublishesLogEvent() {
        MovieSearchCacheService cacheService = mock(MovieSearchCacheService.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        MovieSearchServiceImpl service = new MovieSearchServiceImpl(cacheService, eventPublisher);
        List<MovieSummary> results = List.of(new MovieSummary("Inception", "2010", List.of("Christopher Nolan")));

        when(cacheService.searchMoviesCached(MovieSource.OMDB, "  Inception  ")).thenReturn(results);

        List<MovieSummary> actual = service.searchMovies(MovieSource.OMDB, "  Inception  ");

        assertSame(results, actual);
        verify(cacheService).searchMoviesCached(MovieSource.OMDB, "  Inception  ");
        verify(eventPublisher).publishEvent(eq(new MovieSearchLogEvent(MovieSource.OMDB, "  Inception  ", "inception", 1)));
    }

    @Test
    void searchMoviesPublishesEventWithZeroResultCount() {
        MovieSearchCacheService cacheService = mock(MovieSearchCacheService.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        MovieSearchServiceImpl service = new MovieSearchServiceImpl(cacheService, eventPublisher);

        when(cacheService.searchMoviesCached(MovieSource.TMDB, "Unknown")).thenReturn(List.of());

        service.searchMovies(MovieSource.TMDB, "Unknown");

        verify(eventPublisher).publishEvent(eq(new MovieSearchLogEvent(MovieSource.TMDB, "Unknown", "unknown", 0)));
    }
}
