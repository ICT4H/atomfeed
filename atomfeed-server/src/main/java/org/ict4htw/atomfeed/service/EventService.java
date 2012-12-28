package org.ict4htw.atomfeed.service;

import org.ict4htw.atomfeed.mapper.EventRecordMapper;
import org.ict4htw.atomfeed.repository.AllEventRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    private AllEventRecords allEventRecords;

    public void notify (Event event) {
        allEventRecords.add(EventRecordMapper.map(event));
    }

}
