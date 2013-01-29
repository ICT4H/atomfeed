package org.ict4htw.atomfeed.server.service;

import org.ict4htw.atomfeed.server.mapper.EventRecordMapper;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;

public class EventService {
    private AllEventRecords allEventRecords;

    public EventService(AllEventRecords allEventRecords) {
        this.allEventRecords = allEventRecords;
    }

    public void notify (Event event) {
        allEventRecords.add(EventRecordMapper.map(event));
    }
}