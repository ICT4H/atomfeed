package org.ict4htw.atomfeed.server.service;

import org.ict4htw.atomfeed.server.mapper.EventRecordMapper;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private AllEventRecords allEventRecords;

    @Autowired
    public EventService(AllEventRecords allEventRecords) {
        this.allEventRecords = allEventRecords;
    }

    public void notify (Event event) {
        allEventRecords.add(EventRecordMapper.map(event));
    }
}