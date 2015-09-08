package org.ict4h.atomfeed.server.service;

import org.ict4h.atomfeed.server.domain.EventRecordQueueItem;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;

import java.util.Date;

public class EventServiceImpl implements EventService {

    private AllEventRecordsQueue allEventRecordsQueue;

    public EventServiceImpl(AllEventRecordsQueue allEventRecordsQueue) {
        this.allEventRecordsQueue = allEventRecordsQueue;
    }

    public void notify(Event event) {
        EventRecordQueueItem eventRecordQueueItem = new EventRecordQueueItem(
                event.getUuid(),
                event.getTitle(),
                event.getUri(),
                event.getContents(),
                new Date(),
                event.getCategory());

        allEventRecordsQueue.add(eventRecordQueueItem);
    }
}