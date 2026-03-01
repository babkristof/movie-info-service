package com.assignment.informula.movieinfoservice.service;

import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import com.assignment.informula.movieinfoservice.event.MovieSearchLogEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieSearchServiceImpl implements MovieSearchService {
    private final MovieSearchCacheService cacheService;
    private final ApplicationEventPublisher eventPublisher;

    public MovieSearchServiceImpl(MovieSearchCacheService cacheService,
                                  ApplicationEventPublisher eventPublisher) {
        this.cacheService = cacheService;
        this.eventPublisher = eventPublisher;
    }
    @Override
    public List<MovieSummary> searchMovies(MovieSource movieSource, String movieTitle) {
        List<MovieSummary> results = cacheService.searchMoviesCached(movieSource, movieTitle);

        String normalized = MovieTitleNormalizer.normalize(movieTitle);
        eventPublisher.publishEvent(new MovieSearchLogEvent(
                movieSource,
                movieTitle,
                normalized,
                results.size()
        ));

        return results;
    }
}
