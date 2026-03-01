package com.assignment.informula.movieinfoservice.client.omdb;

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
public class OmdbMovieProvider implements MovieProvider {
    private static final Logger log = LoggerFactory.getLogger(OmdbMovieProvider.class);
    private static final int MAX_RESULTS = 10;
    private static final int CONCURRENCY = 5;

    private final WebClient omdbWebClient;
    private final OmdbMapper mapper;
    private final String apiKey;
    private final Duration timeout;

    public OmdbMovieProvider(
            @Qualifier("omdbWebClient") WebClient omdbWebClient,
            OmdbMapper mapper,
            @Value("${app.external.omdb.api-key}") String apiKey,
            @Value("${app.external.timeout:3s}") Duration timeout
    ) {
        this.omdbWebClient = omdbWebClient;
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.timeout = timeout;
    }

    @Override
    public MovieSource supports() {
        return MovieSource.OMDB;
    }

    @Override
    public List<MovieSummary> search(String title) {
        log.info("Cache miss, querying OMDB for title='{}'", title);

        return omdbWebClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("s", title).queryParam("apikey", apiKey).build())
                .retrieve()
                .bodyToMono(OmdbDtos.OmdbSearchResponse.class)
                .timeout(timeout)
                .flatMapMany(response -> {
                    if (response == null || response.search() == null || "False".equalsIgnoreCase(response.response())) {
                        return Flux.empty();
                    }
                    return Flux.fromIterable(response.search());
                })
                .take(MAX_RESULTS)
                .flatMap(item -> fetchDetails(item.imdbId()), CONCURRENCY)
                .map(mapper::toMovieSummary)
                .collectList()
                .onErrorMap(TimeoutException.class, ex -> new ExternalApiTimeoutException("OMDB request timed out", ex))
                .onErrorMap(ex -> ex instanceof ExternalApiTimeoutException ? ex : new ExternalApiException("OMDB request failed", ex))
                .block();
    }

    private Mono<OmdbDtos.OmdbDetailResponse> fetchDetails(String imdbId) {
        if (imdbId == null || imdbId.isBlank()) {
            return Mono.empty();
        }
        return omdbWebClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("i", imdbId).queryParam("apikey", apiKey).build())
                .retrieve()
                .bodyToMono(OmdbDtos.OmdbDetailResponse.class)
                .timeout(timeout)
                .onErrorResume(ex -> {
                    log.debug("OMDB detail fetch failed, imdbId={}", imdbId, ex);
                    return Mono.empty();
                })
                .flatMap(detail -> {
                    if (detail == null || "False".equalsIgnoreCase(detail.response())) {
                        return Mono.empty();
                    }
                    return Mono.just(detail);
                });
    }
}
