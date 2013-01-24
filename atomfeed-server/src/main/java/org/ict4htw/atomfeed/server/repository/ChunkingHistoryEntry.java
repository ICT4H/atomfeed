package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;

import java.util.List;

public interface ChunkingHistoryEntry {
    List<NumberBasedChunkingHistoryEntry> all();
}
