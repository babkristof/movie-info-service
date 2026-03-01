package com.assignment.informula.movieinfoservice.service;

import com.assignment.informula.movieinfoservice.domain.MovieProvider;
import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieSearchServiceImpl implements MovieSearchService {
    private static final Logger log = LoggerFactory.getLogger(MovieSearchServiceImpl.class);

    private final MovieProviderRegistry providerRegistry;

    public MovieSearchServiceImpl(MovieProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    @Override
    @Cacheable(
            cacheNames = "movie-search",
            key = "#p0.name().toLowerCase() + '::' + T(com.assignment.informula.movieinfoservice.service.MovieTitleNormalizer).normalize(#p1)"
    )
    public List<MovieSummary> searchMovies(MovieSource movieSource, String movieTitle) {
        MovieProvider provider = providerRegistry.resolve(movieSource);
        return provider.search(movieTitle);
    }
}
