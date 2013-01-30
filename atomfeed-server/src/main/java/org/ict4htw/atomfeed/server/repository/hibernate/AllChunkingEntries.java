package org.ict4htw.atomfeed.server.repository.hibernate;

import java.util.List;

import org.ict4htw.atomfeed.server.repository.ChunkingHistories;

public class AllChunkingEntries implements ChunkingHistories {

    private DataAccessTemplate template;

    public AllChunkingEntries(DataAccessTemplate template) {
        this.template = template;
    }
    protected AllChunkingEntries(){}

    public <T> List<T> all(Class<T> t) {
        return template.loadAll(t);
    }
}
