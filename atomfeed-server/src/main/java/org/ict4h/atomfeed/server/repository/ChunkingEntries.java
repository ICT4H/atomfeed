package org.ict4h.atomfeed.server.repository;

import java.util.List;

public interface ChunkingEntries {
    public <T> List<T> all(Class<T> t);
}
