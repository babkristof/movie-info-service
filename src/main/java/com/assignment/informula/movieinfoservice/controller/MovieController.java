package com.assignment.informula.movieinfoservice.controller;

import com.assignment.informula.movieinfoservice.controller.dto.ErrorResponseDto;
import com.assignment.informula.movieinfoservice.controller.dto.MovieDto;
import com.assignment.informula.movieinfoservice.controller.dto.MoviesResponseDto;
import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import com.assignment.informula.movieinfoservice.service.MovieSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Search movies by title",
            description = "Returns matching movies from the selected external provider."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movies fetched successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MoviesResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid provider, missing parameter, or invalid title",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "External provider request failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "External provider request timed out",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/{movieTitle}")
    public MoviesResponseDto searchMovies(
            @Parameter(
                    description = "Movie title to search for",
                    example = "Inception"
            )
            @PathVariable
            @NotBlank(message = "Movie title must not be blank")
            @Size(max = 100, message = "Movie title must be at most 100 characters")
            String movieTitle,
            @Parameter(
                    description = "External movie provider to use",
                    required = true,
                    schema = @Schema(allowableValues = {"omdb", "tmdb"}),
                    example = "omdb"
            )
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
