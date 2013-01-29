package org.ict4htw.atomfeed.server.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository(value = "allChunkingEntries")
@Transactional
public class AllChunkingEntries implements ChunkingHistories {

    private DataAccessTemplate template;

    @Autowired
    public AllChunkingEntries(DataAccessTemplate template) {
        this.template = template;
    }
    protected AllChunkingEntries(){}

    public <T> List<T> all(Class<T> t) {
        return template.loadAll(t);
    }
}
