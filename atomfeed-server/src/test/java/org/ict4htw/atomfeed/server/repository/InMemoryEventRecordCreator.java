package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.server.domain.EventRecord;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

public class InMemoryEventRecordCreator extends EventRecordCreator {

    public InMemoryEventRecordCreator(AllEventRecords allEventRecords) {
        super(allEventRecords);
    }

    public void create(int numOfEvents) throws URISyntaxException {
        addEvents(numOfEvents);
    }
    
    private void addEvents(int numOfEvents) throws URISyntaxException {
    	Calendar cal = Calendar.getInstance();
        for (int index= 1; index <= numOfEvents; index++) {
        	cal.add(Calendar.SECOND, 1);
            allEventRecords.add(new EventRecord(
            		"uuid" + index,
                    "title" + index,
            		new URI("http://foo.bar/" + index),
            		"Event " + index + "'s contents.",
                    cal.getTime()));
        }
    }
}