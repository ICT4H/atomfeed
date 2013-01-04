package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.EventRecordComparator;

import java.util.*;

public class AllEventRecordsStub extends AllEventRecords {
    private Map<String, EventRecord> eventRecords = new HashMap<>();

    @Override
    public void add(EventRecord eventRecord) {
        eventRecords.put(eventRecord.getUuid(), eventRecord);
    }

    @Override
    public EventRecord get(String uuid) {
        return eventRecords.get(uuid);
    }

    @Override
    public int getTotalCount() {
        return eventRecords.size();
    }

    @Override
    public List<EventRecord> getEventsFromNumber(int startNumber, int numberOfEvents) {
        Collection<EventRecord> values = eventRecords.values();
        ArrayList<EventRecord> eventRecordList = new ArrayList<>(values);
        Collections.sort(eventRecordList, new EventRecordComparator());
        int numberOfItems = (startNumber + numberOfEvents) > eventRecordList.size() ? (eventRecordList.size() - startNumber) : numberOfEvents;
        return eventRecordList.subList(startNumber, startNumber + numberOfItems);
    }
}