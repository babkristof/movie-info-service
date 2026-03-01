package com.assignment.informula.movieinfoservice.repository;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "movie_search_log",
        indexes = {
        @Index(name = "idx_msl_api_searched_at", columnList = "api_name,searched_at"),
        @Index(name = "idx_msl_normalized_title", columnList = "normalized_title"),
        @Index(name = "idx_msl_searched_at", columnList = "searched_at")
        })
public class MovieSearchLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false, length = 20)
    private String apiName;

    // raw title the user searched for (as received)
    @Column(name = "movie_title", nullable = false, length = 255)
    private String movieTitle;

    @Column(name = "normalized_title", nullable = false, length = 255)
    private String normalizedTitle;

    @Column(name = "searched_at", nullable = false)
    private Instant searchedAt;

    // number of results returned by OUR API (after MAX_RESULTS)
    @Column(name = "returned_count", nullable = false)
    private int returnedCount;


    public Long getId() {
        return id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getNormalizedTitle() {
        return normalizedTitle;
    }

    public void setNormalizedTitle(String normalizedTitle) {
        this.normalizedTitle = normalizedTitle;
    }

    public Instant getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(Instant searchedAt) {
        this.searchedAt = searchedAt;
    }

    public int getReturnedCount() {
        return returnedCount;
    }

    public void setReturnedCount(int returnedCount) {
        this.returnedCount = returnedCount;
    }
}