package com.assignment.informula.movieinfoservice.domain;

import java.util.List;

public interface MovieProvider {
    MovieSource supports();

    List<MovieSummary> search(String title);
}
