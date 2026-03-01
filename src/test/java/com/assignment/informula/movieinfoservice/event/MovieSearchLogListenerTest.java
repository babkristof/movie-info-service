package com.assignment.informula.movieinfoservice.event;

import com.assignment.informula.movieinfoservice.domain.MovieSource;
import com.assignment.informula.movieinfoservice.repository.MovieSearchLogEntity;
import com.assignment.informula.movieinfoservice.repository.MovieSearchLogRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MovieSearchLogListenerTest {

    @Test
    void handleSavesMappedEntity() {
        MovieSearchLogRepository repository = mock(MovieSearchLogRepository.class);
        MovieSearchLogListener listener = new MovieSearchLogListener(repository);
        MovieSearchLogEvent event = new MovieSearchLogEvent(MovieSource.OMDB, " Inception ", "inception", 3);

        listener.handle(event);

        verify(repository).save(any(MovieSearchLogEntity.class));
        verify(repository).save(org.mockito.ArgumentMatchers.argThat(entity -> {
            assertEquals("omdb", entity.getApiName());
            assertEquals(" Inception ", entity.getMovieTitle());
            assertEquals("inception", entity.getNormalizedTitle());
            assertEquals(3, entity.getReturnedCount());
            assertNotNull(entity.getSearchedAt());
            return true;
        }));
    }

    @Test
    void handleSwallowsRepositoryFailures() {
        MovieSearchLogRepository repository = mock(MovieSearchLogRepository.class);
        doThrow(new RuntimeException("db down")).when(repository).save(any(MovieSearchLogEntity.class));
        MovieSearchLogListener listener = new MovieSearchLogListener(repository);

        listener.handle(new MovieSearchLogEvent(MovieSource.TMDB, "Inception", "inception", 1));

        verify(repository, times(1)).save(any(MovieSearchLogEntity.class));
    }
}
