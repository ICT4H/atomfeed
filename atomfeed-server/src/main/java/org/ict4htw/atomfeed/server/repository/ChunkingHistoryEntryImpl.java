package org.ict4htw.atomfeed.server.repository;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "chunkingHistoryEntry")
public class ChunkingHistoryEntryImpl implements ChunkingHistoryEntry {
    private HibernateTemplate template;

    @Autowired
    public ChunkingHistoryEntryImpl(HibernateTemplate dataAccessTemplate) {
        this.template = dataAccessTemplate;
    }

    @Override
    public List<NumberBasedChunkingHistoryEntry> all() {
        return template.loadAll(NumberBasedChunkingHistoryEntry.class);
    }
}
