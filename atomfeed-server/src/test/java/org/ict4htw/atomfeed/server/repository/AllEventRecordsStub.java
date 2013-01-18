package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.server.domain.EventArchive;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.EventRecordComparator;

import java.util.*;

public class AllEventRecordsStub implements AllEventRecords {
    private Map<String, EventRecord> eventRecords = new HashMap<String, EventRecord>();

    public void add(EventRecord eventRecord) {
        eventRecords.put(eventRecord.getUuid(), eventRecord);
    }

    public EventRecord get(String uuid) {
        return eventRecords.get(uuid);
    }

    public int getTotalCount() {
        return eventRecords.size();
    }

    public List<EventRecord> getEventsFromNumber(int startNumber, int numberOfEvents) {
        ArrayList<EventRecord> eventRecordList = new ArrayList<EventRecord>(eventRecords.values());
        Collections.sort(eventRecordList, new EventRecordComparator());
        //13 total, starting 11 get 5
        //15 total, starting 11 get 5
        int numberOfItems = (startNumber + numberOfEvents-1) > eventRecordList.size() ? (eventRecordList.size() - (startNumber-1)) : numberOfEvents;
        return eventRecordList.subList(startNumber-1, startNumber + numberOfItems-1);
    }

	@Override
	public int getUnarchivedEventsCount() {
		return 0; //should not return 0
	}

	@Override
	public List<EventRecord> getUnarchivedEvents(int limit) {
		return null; //should not return null
	}

	@Override
	public void save(EventArchive eventArchive) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EventArchive getLatestArchive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(List<EventRecord> eventRecords) {
		// TODO Auto-generated method stub
		
	}

    public EventArchive findArchiveById(String archive_id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EventArchive getWorkingArchive() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EventArchive getArchiveWithId(String archiveId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

	@Override
	public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
		return getEventsFromNumber(first, (last-first +1));
	}
}