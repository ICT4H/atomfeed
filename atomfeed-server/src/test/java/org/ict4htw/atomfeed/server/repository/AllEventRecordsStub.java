package org.ict4htw.atomfeed.server.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.EventRecordComparator;

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
	public void save(List<EventRecord> eventRecords) {
		// TODO Auto-generated method stub
	}
    
	@Override
	public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
		return getEventsFromNumber(first, (last-first +1));
	}
}