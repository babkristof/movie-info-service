package com.assignment.informula.movieinfoservice.service;

import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;

import java.util.List;

public interface MovieSearchService {
    List<MovieSummary> searchMovies(MovieSource movieSource, String movieTitle);
}
