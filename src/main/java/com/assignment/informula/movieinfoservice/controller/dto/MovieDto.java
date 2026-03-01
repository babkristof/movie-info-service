package com.assignment.informula.movieinfoservice.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Movie item")
public record MovieDto(
        @JsonProperty("Title")
        @Schema(name = "Title", example = "Inception")
        String title,

        @JsonProperty("Year")
        @Schema(name = "Year", example = "2010")
        String year,

        @JsonProperty("Director")
        @Schema(name = "Director", example = "[\"Christopher Nolan\"]")
        List<String> directors
) {
}
