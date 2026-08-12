package com.pokedex.core.dto;

import java.util.List;

/**
 * Outcome of a replication run (US03): totals plus a lightweight per-item summary.
 */
public record SyncResult(
        int synced,
        int created,
        int updated,
        List<SyncItem> items) {

    public record SyncItem(Long id, String name, String category) {
    }
}
