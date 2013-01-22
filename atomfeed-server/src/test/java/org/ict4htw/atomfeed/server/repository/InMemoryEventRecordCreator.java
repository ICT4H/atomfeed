package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.server.domain.EventRecord;

import java.net.URI;
import java.net.URISyntaxException;

public class InMemoryEventRecordCreator {
    private AllEventRecords allEventRecords;

    public InMemoryEventRecordCreator(AllEventRecords allEventRecords) {
        this.allEventRecords = allEventRecords;
    }

    public int create(int numOfEvents) throws URISyntaxException {
        addEvents(numOfEvents);
        return allEventRecords.getTotalCount();
    }
    
    private void addEvents(int numOfEvents) throws URISyntaxException {
        for (int idx= 1; idx <= numOfEvents; idx++) {
            allEventRecords.add(new EventRecord(
            		"uuid" + idx, "title" + idx, 
            		new URI("http://uri/"+idx), 
            		"Event" + idx));
        }
    }
}