package com.assignment.informula.movieinfoservice.client.tmdb;

import com.assignment.informula.movieinfoservice.domain.MovieSummary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class TmdbMapper {
    public MovieSummary toMovieSummary(TmdbDtos.TmdbDetailResponse detail, TmdbDtos.TmdbCreditsResponse credits) {
        return new MovieSummary(
                detail.title(),
                extractYear(detail.releaseDate()),
                directorsFromCredits(credits)
        );
    }

    List<String> directorsFromCredits(TmdbDtos.TmdbCreditsResponse credits) {
        if (credits == null || credits.crew() == null) {
            return Collections.emptyList();
        }
        Set<String> unique = new LinkedHashSet<>();
        for (TmdbDtos.TmdbCrewMember member : credits.crew()) {
            if (member == null || member.job() == null || member.name() == null) {
                continue;
            }
            if ("Director".equalsIgnoreCase(member.job()) && !member.name().isBlank()) {
                unique.add(member.name().trim());
            }
        }
        return List.copyOf(unique);
    }

    private String extractYear(String releaseDate) {
        if (releaseDate == null || releaseDate.length() < 4) {
            return "";
        }
        return releaseDate.substring(0, 4);
    }
}
