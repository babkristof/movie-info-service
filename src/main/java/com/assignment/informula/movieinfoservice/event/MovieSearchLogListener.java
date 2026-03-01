package com.assignment.informula.movieinfoservice.event;

import com.assignment.informula.movieinfoservice.repository.MovieSearchLogEntity;
import com.assignment.informula.movieinfoservice.repository.MovieSearchLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MovieSearchLogListener {
    private static final Logger log = LoggerFactory.getLogger(MovieSearchLogListener.class);

    private final MovieSearchLogRepository repository;

    public MovieSearchLogListener(MovieSearchLogRepository repository) {
        this.repository = repository;
    }

    @Async("loggingTaskExecutor")
    @EventListener
    public void handle(MovieSearchLogEvent event) {
        try {
            MovieSearchLogEntity entity = new MovieSearchLogEntity();
            entity.setApiName(event.movieSource().name().toLowerCase());
            entity.setMovieTitle(event.rawTitle());
            entity.setNormalizedTitle(event.normalizedTitle());
            entity.setSearchedAt(Instant.now());
            entity.setReturnedCount(event.returnedCount());
            repository.save(entity);
        } catch (Exception ex) {
            log.warn("Search logging failed. api={} title={}", event.movieSource(), event.rawTitle(), ex);
        }
    }
}
