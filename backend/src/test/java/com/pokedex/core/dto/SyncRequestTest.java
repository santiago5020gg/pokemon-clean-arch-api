package com.pokedex.core.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SyncRequest} normalization: a missing/invalid limit falls back to a
 * sensible default, and the limit is capped so a single sync cannot hammer the PokeAPI
 * (each replicated Pokémon costs ~3 external calls). Gen 1 (151) is the hard upper bound.
 */
class SyncRequestTest {

    @Test
    void limit_defaultsTo20_whenNullOrNonPositive() {
        assertThat(new SyncRequest(null, null).limitOrDefault()).isEqualTo(20);
        assertThat(new SyncRequest(0, null).limitOrDefault()).isEqualTo(20);
        assertThat(new SyncRequest(-5, null).limitOrDefault()).isEqualTo(20);
    }

    @Test
    void limit_passesThroughValuesWithinRange() {
        assertThat(new SyncRequest(50, null).limitOrDefault()).isEqualTo(50);
        assertThat(new SyncRequest(100, null).limitOrDefault()).isEqualTo(100);
        assertThat(new SyncRequest(151, null).limitOrDefault()).isEqualTo(151);
    }

    @Test
    void limit_isCappedAt151() {
        assertThat(new SyncRequest(152, null).limitOrDefault()).isEqualTo(151);
        assertThat(new SyncRequest(100_000, null).limitOrDefault()).isEqualTo(151);
    }

    @Test
    void offset_defaultsToZero_whenNullOrNegative() {
        assertThat(new SyncRequest(null, null).offsetOrDefault()).isZero();
        assertThat(new SyncRequest(null, -3).offsetOrDefault()).isZero();
        assertThat(new SyncRequest(null, 40).offsetOrDefault()).isEqualTo(40);
    }
}
