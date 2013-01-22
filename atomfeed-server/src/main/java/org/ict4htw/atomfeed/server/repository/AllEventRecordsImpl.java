package org.ict4htw.atomfeed.server.repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllEventRecordsImpl implements AllEventRecords {

    private DataAccessTemplate template;

    protected AllEventRecordsImpl() { }

    @Autowired
    public AllEventRecordsImpl(DataAccessTemplate template) {
        this.template = template;
    }

    /* (non-Javadoc)
	 * @see org.ict4htw.atomfeed.server.repository.AllEventRecords#add(org.ict4htw.atomfeed.server.domain.EventRecord)
	 */
    @Override
	public void add(EventRecord eventRecord) {
        template.save(eventRecord);
    }

    /* (non-Javadoc)
	 * @see org.ict4htw.atomfeed.server.repository.AllEventRecords#get(java.lang.String)
	 */
    @Override
	public EventRecord get(String uuid) {
        return (EventRecord) template.getUniqueResult(EventRecord.FIND_BY_UUID, new String[]{"uuid"}, new Object[]{uuid});
    }

    /* (non-Javadoc)
	 * @see org.ict4htw.atomfeed.server.repository.AllEventRecords#getTotalCount()
	 */
    @Override
	public int getTotalCount() {
        Long totalCount = (Long)template.getSessionFactory().openSession().createQuery("select count(*) from EventRecord").uniqueResult();
        return totalCount.intValue();
    }

    /* (non-Javadoc)
	 * @see org.ict4htw.atomfeed.server.repository.AllEventRecords#getEventsFromNumber(int, int)
	 */
    @Override
	public List<EventRecord> getEventsFromNumber(int startNumber, int numberOfEvents) {
        return (List<EventRecord>) template.getSessionFactory().openSession().createQuery(
                "select e from EventRecord e order by e.timeStamp desc")
                .setFirstResult(startNumber).setMaxResults(numberOfEvents).list();
    }

	
	@Override
	public void save(List<EventRecord> eventRecords) {
		template.saveOrUpdateAll(eventRecords);
	}

	@Override
	public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
		Query query = template.getSessionFactory().openSession().createQuery(
                "select er from EventRecord er where er.id >= :first and er.id <= :last order by er.timeStamp asc");
		query.setParameter("first", first);
		query.setParameter("last", last);
		return query.list();
	}

    @Override
    public List<EventRecord> getEventsFromTimeRange(TimeRange timeRange) {
        return Collections.emptyList();
    }
}
