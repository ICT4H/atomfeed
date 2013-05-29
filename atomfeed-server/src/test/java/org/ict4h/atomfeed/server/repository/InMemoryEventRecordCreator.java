package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.server.domain.EventRecord;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

public class InMemoryEventRecordCreator extends EventRecordCreator {

    public InMemoryEventRecordCreator(AllEventRecords allEventRecords) {
        super(allEventRecords);
    }

    public void create(int numOfEvents, String category) throws URISyntaxException {
        addEvents(numOfEvents, category);
    }
    
    private void addEvents(int numOfEvents, String category) throws URISyntaxException {
    	Calendar cal = Calendar.getInstance();
        for (int index= 1; index <= numOfEvents; index++) {
        	cal.add(Calendar.SECOND, 1);
            allEventRecords.add(new EventRecord(
            		"uuid" + index,
                    "title" + index,
            		new URI("http://foo.bar/" + index),
            		"Event " + index + "'s contents.",
                    cal.getTime(), category));
        }
    }

    public void create(EventRecord eventRecord) {
        allEventRecords.add(eventRecord);
    }
}