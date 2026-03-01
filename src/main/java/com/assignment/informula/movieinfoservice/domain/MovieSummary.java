package com.assignment.informula.movieinfoservice.domain;

import java.util.List;

public record MovieSummary(String title, String year, List<String> directors) {
}
