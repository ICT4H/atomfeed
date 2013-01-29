package org.ict4htw.atomfeed.server.repository;

import java.util.List;

public interface ChunkingHistories {
    public <T> List<T> all(Class<T> t);
}
