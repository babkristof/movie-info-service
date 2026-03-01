package com.assignment.informula.movieinfoservice.event;

import com.assignment.informula.movieinfoservice.domain.MovieSource;

public record MovieSearchLogEvent(
        MovieSource movieSource,
        String rawTitle,
        String normalizedTitle,
        int returnedCount
) {}
