package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.EventRecordQueueItem;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

import java.util.*;

public class AllEventRecordsQueueStub implements AllEventRecordsQueue {
    private Map<String, EventRecordQueueItem> eventRecordsQueue = new HashMap<String, EventRecordQueueItem>();

    public void add(EventRecordQueueItem eventRecordQueueItem) {
        eventRecordsQueue.put(eventRecordQueueItem.getUuid(), eventRecordQueueItem);
    }

    public EventRecordQueueItem get(String uuid) {
        return eventRecordsQueue.get(uuid);
    }

    @Override
    public List<EventRecordQueueItem> getAll() {
        List<EventRecordQueueItem> eventRecordQueueItems = new ArrayList<>();
        for (String key : eventRecordsQueue.keySet()) {
            eventRecordQueueItems.add(eventRecordsQueue.get(key));
        }
        return eventRecordQueueItems;
    }

    @Override
    public void delete(String uuid) {
        eventRecordsQueue.remove(uuid);
    }

    public void clear() {
        eventRecordsQueue.clear();
    }
}