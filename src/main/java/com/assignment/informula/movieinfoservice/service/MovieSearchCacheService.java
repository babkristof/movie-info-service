package com.assignment.informula.movieinfoservice.service;

import com.assignment.informula.movieinfoservice.domain.MovieProvider;
import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieSearchCacheService {

    private final MovieProviderRegistry providerRegistry;

    public MovieSearchCacheService(MovieProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    @Cacheable(
            cacheNames = "movie-search",
            key = "#p0.name().toLowerCase() + '::' + T(com.assignment.informula.movieinfoservice.service.MovieTitleNormalizer).normalize(#p1)"
    )
    public List<MovieSummary> searchMoviesCached(MovieSource movieSource, String movieTitle) {
        MovieProvider provider = providerRegistry.resolve(movieSource);
        return provider.search(movieTitle);
    }
}