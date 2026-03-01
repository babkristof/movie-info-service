package com.assignment.informula.movieinfoservice.controller;

import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import com.assignment.informula.movieinfoservice.service.MovieSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@Import(com.assignment.informula.movieinfoservice.exception.GlobalExceptionHandler.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieSearchService movieSearchService;

    @Test
    void searchMoviesReturnsMappedResponse() throws Exception {
        when(movieSearchService.searchMovies(eq(MovieSource.OMDB), eq("Inception")))
                .thenReturn(List.of(new MovieSummary("Inception", "2010", List.of("Christopher Nolan"))));

        mockMvc.perform(get("/movies/{movieTitle}", "Inception").queryParam("api", "omdb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movies[0].Title").value("Inception"))
                .andExpect(jsonPath("$.movies[0].Year").value("2010"))
                .andExpect(jsonPath("$.movies[0].Director[0]").value("Christopher Nolan"));

        verify(movieSearchService).searchMovies(eq(MovieSource.OMDB), eq("Inception"));
    }

    @Test
    void searchMoviesReturnsBadRequestWhenApiIsMissing() throws Exception {
        mockMvc.perform(get("/movies/{movieTitle}", "Inception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("MISSING_PARAMETER"))
                .andExpect(jsonPath("$.error.message").value("Missing required request parameter: api"));
    }

    @Test
    void searchMoviesReturnsBadRequestWhenApiIsBlank() throws Exception {
        mockMvc.perform(get("/movies/{movieTitle}", "Inception").queryParam("api", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_API"))
                .andExpect(jsonPath("$.error.message").value("Movie source is required. Allowed values: omdb, tmdb"));
    }

    @Test
    void searchMoviesReturnsBadRequestWhenApiIsUnsupported() throws Exception {
        mockMvc.perform(get("/movies/{movieTitle}", "Inception").queryParam("api", "imdb"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_API"))
                .andExpect(jsonPath("$.error.message").value("Unsupported movie source: imdb. Allowed values: omdb, tmdb"));
    }

    @Test
    void searchMoviesReturnsBadRequestWhenMovieTitleIsBlank() throws Exception {
        mockMvc.perform(get("/movies/{movieTitle}", "  ").queryParam("api", "omdb"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Movie title must not be blank"));
    }

    @Test
    void searchMoviesReturnsBadRequestWhenMovieTitleIsTooLong() throws Exception {
        String longTitle = "x".repeat(101);

        mockMvc.perform(get("/movies/{movieTitle}", longTitle).queryParam("api", "omdb"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Movie title must be at most 100 characters"));
    }
}
