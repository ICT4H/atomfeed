package org.ict4htw.atomfeed.server.repository;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
