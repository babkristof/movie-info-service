package com.assignment.informula.movieinfoservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API error response")
public record ErrorResponseDto(ErrorBody error) {

    public record ErrorBody(
            @Schema(example = "INVALID_API")
            String code,
            @Schema(example = "Unsupported movieSource")
            String message,
            @Schema(example = "Allowed values: omdb, tmdb")
            String details
    ) {
    }
}
