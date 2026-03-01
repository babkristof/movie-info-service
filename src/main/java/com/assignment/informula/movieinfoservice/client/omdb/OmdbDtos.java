package com.assignment.informula.movieinfoservice.client.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class OmdbDtos {
    private OmdbDtos() {
    }

    public record OmdbSearchResponse(
            @JsonProperty("Search")
            List<OmdbSearchItem> search,
            @JsonProperty("Response")
            String response,
            @JsonProperty("Error")
            String error
    ) {
    }

    public record OmdbSearchItem(
            @JsonProperty("imdbID")
            String imdbId
    ) {
    }

    public record OmdbDetailResponse(
            @JsonProperty("Title")
            String title,
            @JsonProperty("Year")
            String year,
            @JsonProperty("Director")
            String director,
            @JsonProperty("Response")
            String response
    ) {
    }
}
