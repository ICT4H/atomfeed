package org.ict4htw.atomfeed.server.repository.hibernate;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;


public class AllEventRecordsJPA implements AllEventRecords {

	private EntityManager em;

    protected AllEventRecordsJPA() { }

    public AllEventRecordsJPA(EntityManager em) {
        this.em = em;
    }

    /* (non-Javadoc)
	 * @see org.ict4htw.atomfeed.server.repository.AllEventRecords#add(org.ict4htw.atomfeed.server.domain.EventRecord)
	 */
    @Override
	public void add(EventRecord eventRecord) {
        //template.save(eventRecord);
    }

    /* (non-Javadoc)
	 * @see org.ict4htw.atomfeed.server.repository.AllEventRecords#get(java.lang.String)
	 */
    @Override
	public EventRecord get(String uuid) {
    	return findByUuid(uuid);
    }

    /* (non-Javadoc)
	 * @see org.ict4htw.atomfeed.server.repository.AllEventRecords#getTotalCount()
	 */
    @Override
	public int getTotalCount() {
    	int count = ((Number)em.createNamedQuery(EventRecord.TOTAL_COUNT).getSingleResult()).intValue();
        return count;
    }
    
	@Override
	public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
//		Query query = em.createQuery("select er from EventRecord er where er.uuid=:uuid");
//        query.setParameter("uuid",uuid);
//        
//		Query query = template.getSessionFactory().openSession().createQuery(
//                "select er from EventRecord er where er.id >= :first and er.id <= :last order by er.timeStamp asc");
//		query.setParameter("first", first);
//		query.setParameter("last", last);
//		return query.list();
		return null;
	}

    @Override
    public List<EventRecord> getEventsFromTimeRange(TimeRange timeRange) {
        return Collections.emptyList();
    }
    
    private EventRecord findByUuid(String uuid) {
        Query query = em.createQuery("select er from EventRecord er where er.uuid=:uuid");
        query.setParameter("uuid",uuid);
        return executeQuery(query);
    }
    
    private EventRecord executeQuery(Query query) {
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        if(resultList.size() != 1){
            throw new RuntimeException("Found more than one event");
        }
        return (EventRecord)resultList.get(0);
    }
}
