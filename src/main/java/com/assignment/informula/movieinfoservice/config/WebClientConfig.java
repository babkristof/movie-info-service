package com.assignment.informula.movieinfoservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Configuration
public class WebClientConfig {
    private final String omdbBaseUrl;
    private final String tmdbBaseUrl;
    private final Duration timeout;

    public WebClientConfig(
            @Value("${app.external.omdb.base-url}") String omdbBaseUrl,
            @Value("${app.external.tmdb.base-url}") String tmdbBaseUrl,
            @Value("${app.external.timeout:3s}") Duration timeout)
    {
        this.omdbBaseUrl = omdbBaseUrl;
        this.tmdbBaseUrl = tmdbBaseUrl;
        this.timeout = timeout;
    }

    private WebClient buildClient(String baseUrl) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeout.toMillis())
                .responseTimeout(timeout)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(timeout.toMillis(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeout.toMillis(), TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean("omdbWebClient")
    public WebClient omdbWebClient() {
        return buildClient(omdbBaseUrl);
    }

    @Bean("tmdbWebClient")
    public WebClient tmdbWebClient() {
        return buildClient(tmdbBaseUrl);
    }
}
