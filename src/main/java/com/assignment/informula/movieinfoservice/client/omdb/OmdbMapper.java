package com.assignment.informula.movieinfoservice.client.omdb;

import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class OmdbMapper {
    public MovieSummary toMovieSummary(OmdbDtos.OmdbDetailResponse detail) {
        return new MovieSummary(
                detail.title(),
                detail.year(),
                splitDirectors(detail.director())
        );
    }

    List<String> splitDirectors(String directorsRaw) {
        if (directorsRaw == null || directorsRaw.isBlank() || "N/A".equalsIgnoreCase(directorsRaw.trim())) {
            return Collections.emptyList();
        }
        return Arrays.stream(directorsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
