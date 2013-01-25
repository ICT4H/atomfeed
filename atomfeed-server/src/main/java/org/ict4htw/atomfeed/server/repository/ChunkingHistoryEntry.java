package org.ict4htw.atomfeed.server.repository;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "chunkingHistoryEntry")
public class ChunkingHistoryEntry {
    private HibernateTemplate template;

    @Autowired
    public ChunkingHistoryEntry(HibernateTemplate dataAccessTemplate) {
        this.template = dataAccessTemplate;
    }

    public <T> List<?> all(Class<T> t) {
        return template.loadAll(t);
    }
}
