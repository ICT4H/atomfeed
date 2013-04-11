package org.ict4h.atomfeed.server.repository;


import org.ict4h.atomfeed.server.domain.EventRecord;

import java.net.URI;
import java.net.URISyntaxException;

public class DbEventRecordCreator extends EventRecordCreator{

    public DbEventRecordCreator(AllEventRecords allEventRecords) {
        super(allEventRecords);
    }

    public EventRecord create(String uuid, String title, String url, String contents) throws URISyntaxException {
        EventRecord eventRecord = new EventRecord(uuid, title, new URI(url), contents, null, "");
        allEventRecords.add(eventRecord);
        return eventRecord;
    }
}
