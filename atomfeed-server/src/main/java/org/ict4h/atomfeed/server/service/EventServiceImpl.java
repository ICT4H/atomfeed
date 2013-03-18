package org.ict4h.atomfeed.server.service;

import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.repository.AllEventRecords;

import java.util.Date;

public class EventServiceImpl implements EventService {
    private AllEventRecords allEventRecords;

    public EventServiceImpl(AllEventRecords allEventRecords) {
        this.allEventRecords = allEventRecords;
    }

    public void notify(Event event) {
        EventRecord eventRecord = new EventRecord(
                event.getUuid(),
                event.getTitle(),
                event.getUri(),
                event.getContents(),
                new Date());

        allEventRecords.add(eventRecord);
    }
}