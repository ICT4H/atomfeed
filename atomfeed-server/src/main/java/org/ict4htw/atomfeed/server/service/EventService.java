package org.ict4htw.atomfeed.server.service;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;

import java.util.Date;

public class EventService {
    private AllEventRecords allEventRecords;

    public EventService(AllEventRecords allEventRecords) {
        this.allEventRecords = allEventRecords;
    }

    public void notify(Event event) {
        EventRecord eventRecord = new EventRecord(
                event.getUuid(),
                event.getTitle(),
                event.getUri(),
                event.getEventObject(),
                new Date());

        allEventRecords.add(eventRecord);
    }
}