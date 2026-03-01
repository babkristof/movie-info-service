package com.assignment.informula.movieinfoservice.service;

import com.assignment.informula.movieinfoservice.domain.MovieProvider;
import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.exception.MovieProviderNotFoundException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MovieProviderRegistry {
    private final Map<MovieSource, MovieProvider> providersByMovieSource;

    public MovieProviderRegistry(List<MovieProvider> providers) {
        this.providersByMovieSource = new EnumMap<>(MovieSource.class);
        for (MovieProvider provider : providers) {
            this.providersByMovieSource.put(provider.supports(), provider);
        }
    }

    public MovieProvider resolve(MovieSource movieSource) {
        MovieProvider provider = providersByMovieSource.get(movieSource);
        if (provider == null) {
            throw new MovieProviderNotFoundException("No provider available for api: " + movieSource);
        }
        return provider;
    }
}
