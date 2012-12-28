package org.ict4htw.atomfeed.repository;

import org.hibernate.criterion.Projections;
import org.ict4htw.atomfeed.domain.EventRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@Repository
@Transactional
public class AllEventRecords {

    private DataAccessTemplate template;

    public AllEventRecords() { }

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

    public int getTotalCount() {
        Long totalCount = (Long)template.getSessionFactory().openSession().createQuery("select count(*) from EventRecord").uniqueResult();
        return totalCount.intValue();
    }

    public List<EventRecord> getEventsFromNumber(int startNumber, int numberOfEvents) {
//        return (List<EventRecord>)template.findByNamedQueryAndNamedParam(EventRecord.FIND_FROM_START_NUMBER,
//                new String[] { "limit", "offset" }, new Object[] { numberOfEvents, startNumber });

        return (List<EventRecord>) template.getSessionFactory().openSession().createQuery(
                "select e from EventRecord e order by e.timeStamp desc")
                .setFirstResult(startNumber - 1).setMaxResults(numberOfEvents).list();
    }
}
