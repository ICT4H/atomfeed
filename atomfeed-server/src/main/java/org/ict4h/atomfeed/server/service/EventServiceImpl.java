package org.ict4h.atomfeed.server.service;

import org.ict4h.atomfeed.server.domain.EventRecordQueueItem;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public class EventServiceImpl implements EventService {

    private AllEventRecordsQueue allEventRecordsQueue;

    public EventServiceImpl(AllEventRecordsQueue allEventRecordsQueue) {
        this.allEventRecordsQueue = allEventRecordsQueue;
    }

    public void notify(Event event) {
        Date eventTime = event.getTimeStamp() != null ? toDefaultZoneDate(event.getTimeStamp()) : new Date();
        EventRecordQueueItem eventRecordQueueItem = new EventRecordQueueItem(
                event.getUuid(),
                event.getTitle(),
                event.getUri(),
                event.getContents(),
                eventTime,
                event.getCategory(),
                event.getTags());
        allEventRecordsQueue.add(eventRecordQueueItem);
    }

    private Date toDefaultZoneDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}