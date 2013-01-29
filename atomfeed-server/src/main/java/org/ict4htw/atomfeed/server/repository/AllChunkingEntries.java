package org.ict4htw.atomfeed.server.repository;

import java.util.List;

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
