package com.assignment.informula.movieinfoservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Movies response")
public record MoviesResponseDto(List<MovieDto> movies) {
}
