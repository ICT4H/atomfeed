package org.ict4htw.atomfeed.server.repository;


import org.ict4htw.atomfeed.server.domain.EventRecord;

import java.net.URI;
import java.net.URISyntaxException;

public class DbEventRecordCreator extends EventRecordCreator{

    public DbEventRecordCreator(AllEventRecords allEventRecords) {
        super(allEventRecords);
    }

    public void create(String uuid, String title, String url, String contents) throws URISyntaxException {
        allEventRecords.add(new EventRecord(uuid,title,new URI(url),contents,null));
    }
}
