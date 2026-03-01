package com.assignment.informula.movieinfoservice.client.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class TmdbDtos {
    private TmdbDtos() {
    }

    public record TmdbSearchResponse(List<TmdbSearchItem> results) {
    }

    public record TmdbSearchItem(Long id) {
    }

    public record TmdbDetailResponse(
            Long id,
            String title,
            @JsonProperty("release_date")
            String releaseDate
    ) {
    }

    public record TmdbCreditsResponse(List<TmdbCrewMember> crew) {
    }

    public record TmdbCrewMember(String job, String name) {
    }
}
