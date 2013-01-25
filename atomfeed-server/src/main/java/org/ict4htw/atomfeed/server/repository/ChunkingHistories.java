package org.ict4htw.atomfeed.server.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChunkingHistories {
    public <T> List<T> all(Class<T> t);
}
