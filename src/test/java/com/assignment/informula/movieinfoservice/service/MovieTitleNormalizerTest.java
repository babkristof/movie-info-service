package com.assignment.informula.movieinfoservice.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieTitleNormalizerTest {

    @Test
    void normalizeReturnsEmptyStringForNull() {
        assertEquals("", MovieTitleNormalizer.normalize(null));
    }

    @Test
    void normalizeTrimsLowercasesAndCollapsesWhitespace() {
        assertEquals("the dark knight", MovieTitleNormalizer.normalize("  The   Dark   Knight  "));
    }

    @Test
    void normalizePreservesInternalNonWhitespaceCharacters() {
        assertEquals("wall-e", MovieTitleNormalizer.normalize(" WALL-E "));
    }
}
