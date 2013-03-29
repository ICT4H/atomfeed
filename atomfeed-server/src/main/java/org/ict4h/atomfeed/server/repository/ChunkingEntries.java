package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;

import java.util.List;

public interface ChunkingEntries {
    public List<ChunkingHistoryEntry> all();
}
