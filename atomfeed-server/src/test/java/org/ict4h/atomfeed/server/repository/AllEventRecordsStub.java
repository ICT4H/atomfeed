package org.ict4h.atomfeed.server.repository;

import org.hamcrest.Matchers;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.EventRecordComparator;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

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

    @Override
    public int getTotalCountForCategory(String category) {
        return filterEventsBasedOnCategory(category,eventRecords.values()).size();
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
    public List<EventRecord> getEventsFromRangeForCategory(String category, Integer offset, Integer limit) {
        Collection<EventRecord> values = eventRecords.values();
        if(values.isEmpty()){
            return new ArrayList<>();
        }
        return filterEventsBasedOnCategory(category, values)
                .subList(offset, Math.min(offset + limit, values.size()));
    }

    private List<EventRecord> filterEventsBasedOnCategory(String category, Collection<EventRecord> values) {
        if(category == null){
            return new ArrayList<>(values);
        }
        return filter(having(on(EventRecord.class).getCategory(), equalTo(category)), values);
    }

    @Override
    public List<EventRecord> getEventsFromTimeRange(TimeRange timeRange) {
        ArrayList<EventRecord> recordsWithinTimeRange = new ArrayList<EventRecord>();
        for (EventRecord record : eventRecords.values()) {
            if (timeRange.getStartTimestamp().before(record.getTimeStamp())
                    && timeRange.getEndTimestamp().after(record.getTimeStamp())) {
                recordsWithinTimeRange.add(record);
            }
        }
        return recordsWithinTimeRange;
    }

    public void clear() {
        eventRecords.clear();
    }
}