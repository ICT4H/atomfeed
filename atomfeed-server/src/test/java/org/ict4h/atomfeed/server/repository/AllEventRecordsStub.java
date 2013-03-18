package org.ict4h.atomfeed.server.repository;

import java.util.*;

import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.EventRecordComparator;
import org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;

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
    
	@Override
	public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
          ArrayList<EventRecord> eventRecordList = new ArrayList<EventRecord>(eventRecords.values());
          Collections.sort(eventRecordList, new EventRecordComparator());
          int effectiveLast = Math.min(last, eventRecordList.size());
          if(eventRecordList.isEmpty()){
              return eventRecordList;
          }
          return eventRecordList.subList(first - 1, effectiveLast);
	}

    @Override
    public List<EventRecord> getEventsFromTimeRange(TimeRange timeRange) {
        ArrayList<EventRecord> recordsWithinTimeRange = new ArrayList<EventRecord>();
        for (EventRecord record : eventRecords.values()) {
            //LocalDateTime recordTime = new LocalDateTime(record.getTimeStamp().getTime());
            if (timeRange.getStartTime().toDate().before(record.getTimeStamp())
                    && timeRange.getEndTime().toDate().after(record.getTimeStamp())) {
                recordsWithinTimeRange.add(record);
            }
        }
        return recordsWithinTimeRange;
    }

    public void clear() {
        eventRecords.clear();
    }
}