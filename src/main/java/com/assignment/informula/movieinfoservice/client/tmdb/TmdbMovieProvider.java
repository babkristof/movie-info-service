package com.assignment.informula.movieinfoservice.client.tmdb;

import com.assignment.informula.movieinfoservice.domain.MovieProvider;
import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import com.assignment.informula.movieinfoservice.exception.ExternalApiException;
import com.assignment.informula.movieinfoservice.exception.ExternalApiTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
public class TmdbMovieProvider implements MovieProvider {
    private static final Logger log = LoggerFactory.getLogger(TmdbMovieProvider.class);
    private static final int MAX_RESULTS = 10;
    private static final int CONCURRENCY = 5;

    private final WebClient tmdbWebClient;
    private final TmdbMapper mapper;
    private final String apiKey;
    private final Duration timeout;

    public TmdbMovieProvider(
            @Qualifier("tmdbWebClient") WebClient tmdbWebClient,
            TmdbMapper mapper,
            @Value("${app.external.tmdb.api-key}") String apiKey,
            @Value("${app.external.timeout:3s}") Duration timeout
    ) {
        this.tmdbWebClient = tmdbWebClient;
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.timeout = timeout;
    }

    @Override
    public MovieSource supports() {
        return MovieSource.TMDB;
    }

    @Override
    public List<MovieSummary> search(String title) {
        log.info("Cache miss, querying TMDB for title='{}'", title);

        return tmdbWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("query", title)
                        .queryParam("include_adult", true)
                        .build())
                .retrieve()
                .bodyToMono(TmdbDtos.TmdbSearchResponse.class)
                .timeout(timeout)
                .flatMapMany(response -> response == null || response.results() == null
                        ? Flux.empty()
                        : Flux.fromIterable(response.results()))
                .take(MAX_RESULTS)
                .flatMap(item -> fetchDetailAndCredits(item.id()), CONCURRENCY)
                .collectList()
                .onErrorMap(TimeoutException.class, ex -> new ExternalApiTimeoutException("TMDB request timed out", ex))
                .onErrorMap(ex -> ex instanceof ExternalApiTimeoutException ? ex : new ExternalApiException("TMDB request failed", ex))
                .block();
    }

    private Mono<MovieSummary> fetchDetailAndCredits(Long movieId) {
        if (movieId == null) {
            return Mono.empty();
        }

        Mono<TmdbDtos.TmdbDetailResponse> detailsMono = tmdbWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("api_key", apiKey)
                        .build(movieId))
                .retrieve()
                .bodyToMono(TmdbDtos.TmdbDetailResponse.class)
                .timeout(timeout);

        Mono<TmdbDtos.TmdbCreditsResponse> creditsMono = tmdbWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}/credits")
                        .queryParam("api_key", apiKey)
                        .build(movieId))
                .retrieve()
                .bodyToMono(TmdbDtos.TmdbCreditsResponse.class)
                .timeout(timeout);

        return Mono.zip(detailsMono, creditsMono)
                .map(tuple -> mapper.toMovieSummary(tuple.getT1(), tuple.getT2()))
                .onErrorResume(ex -> Mono.empty());
    }
}
