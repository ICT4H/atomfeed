package org.ict4htw.atomfeed.repository;

import org.ict4htw.atomfeed.domain.EventRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllEventRecords {

    private DataAccessTemplate template;

    public AllEventRecords() {
    }

    @Autowired
    public AllEventRecords(DataAccessTemplate template) {
        this.template = template;
    }

    public void add(EventRecord eventRecord) {
        template.save(eventRecord);
    }

    public EventRecord get(String uuid) {
        return (EventRecord) template.getUniqueResult(EventRecord.FIND_BY_UUID, new String[]{"uuid"}, new Object[]{uuid});
    }
}
