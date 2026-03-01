package com.assignment.informula.movieinfoservice.controller;

import com.assignment.informula.movieinfoservice.controller.dto.MovieDto;
import com.assignment.informula.movieinfoservice.controller.dto.MoviesResponseDto;
import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import com.assignment.informula.movieinfoservice.service.MovieSearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/movies", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class MovieController {
    private final MovieSearchService movieSearchService;

    public MovieController(MovieSearchService movieSearchService) {
        this.movieSearchService = movieSearchService;
    }

    @GetMapping("/{movieTitle}")
    public MoviesResponseDto searchMovies(
            @PathVariable
            @NotBlank(message = "Movie title must not be blank")
            @Size(max = 100, message = "Movie title must be at most 100 characters")
            String movieTitle,
            @RequestParam("api") String api
    ) {
        MovieSource movieSource = MovieSource.from(api);
        List<MovieSummary> results = movieSearchService.searchMovies(movieSource, movieTitle);

        List<MovieDto> movies = results.stream()
                .map(m -> new MovieDto(m.title(), m.year(), m.directors()))
                .toList();
        return new MoviesResponseDto(movies);
    }
}
