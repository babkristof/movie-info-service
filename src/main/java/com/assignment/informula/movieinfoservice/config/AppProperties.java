package com.assignment.informula.movieinfoservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.cache")
public record AppProperties(Duration ttl) {
}
