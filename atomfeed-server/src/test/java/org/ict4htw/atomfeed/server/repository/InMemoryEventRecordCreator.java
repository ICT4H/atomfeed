package org.ict4htw.atomfeed.server.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import org.ict4htw.atomfeed.server.domain.EventRecord;

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
    	Calendar cal = Calendar.getInstance();
        for (int idx= 1; idx <= numOfEvents; idx++) {
        	cal.add(Calendar.SECOND, 1);
            allEventRecords.add(new EventRecord(
            		"uuid" + idx, "title" + idx, 
            		new URI("http://foo.bar/"+idx), 
            		"Event" + idx, cal.getTime()) );
        }
    }
}