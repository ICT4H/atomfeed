package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.server.domain.EventArchive;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
//        return (List<EventRecord>)template.findByNamedQueryAndNamedParam(EventRecord.FIND_FROM_START_NUMBER,
//                new String[] { "limit", "offset" }, new Object[] { numberOfEvents, startNumber });

        return (List<EventRecord>) template.getSessionFactory().openSession().createQuery(
                "select e from EventRecord e order by e.timeStamp desc")
                .setFirstResult(startNumber).setMaxResults(numberOfEvents).list();
    }

	/* (non-Javadoc)
	 * @see org.ict4htw.atomfeed.server.repository.AllEventRecords#getRecentFeedCount()
	 */
	@Override
	public int getUnarchivedEventsCount() {
		Long totalCount = (Long)template.getSessionFactory().openSession().createQuery("select count(*) from EventRecord where archiveId is null").uniqueResult();
        return totalCount.intValue();
	}

	@Override
	public List<EventRecord> getUnarchivedEvents(int limit) {
		return (List<EventRecord>) template.getSessionFactory().openSession().createQuery(
				"select e from EventRecord e where e.archiveId is null order by e.timeStamp asc").setMaxResults(limit).list();
		
	}

	@Override
	public void save(EventArchive eventArchive) {
		template.save(eventArchive);
	}

	@Override
	public EventArchive getLatestArchive() {
		List<EventArchive> archives = template.getSessionFactory().openSession().createQuery(
                "select ea from EventArchive ea order by ea.timeStamp desc").setMaxResults(1).list();
		return ((archives != null) && (archives.size() > 0)) ? archives.get(0) : null;
		
		
	}

	@Override
	public void save(List<EventRecord> eventRecords) {
		template.saveOrUpdateAll(eventRecords);
	}

	@Override
	public EventArchive findArchiveById(String archive_id) {
        List<EventArchive> archives = template.getSessionFactory().openSession().createQuery(
                "select ea from EventArchive ea where ea.archiveId = '"+ archive_id +"'").setMaxResults(1).list();
        return ((archives != null) && (archives.size() > 0)) ? archives.get(0) : null;
    }
}
