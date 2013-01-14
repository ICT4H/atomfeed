package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.server.domain.EventRecord;

import java.net.URI;
import java.net.URISyntaxException;

public class InMemoryEventRecordCreator {
    private AllEventRecords allEventRecords;

    public InMemoryEventRecordCreator(AllEventRecords allEventRecords) {
        this.allEventRecords = allEventRecords;
    }

    public int create() throws URISyntaxException {
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", new URI("http://uri"), "dadsas");
        EventRecord eventRecordAdded3 = new EventRecord("uuid3", "title", new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded4 = new EventRecord("uuid4", "title", new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded5 = new EventRecord("uuid5", "title", new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded6 = new EventRecord("uuid6", "title", new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded7 = new EventRecord("uuid7", "title", new URI("http://uri"), "asdasd");

        allEventRecords.add(eventRecordAdded1);
        allEventRecords.add(eventRecordAdded2);
        allEventRecords.add(eventRecordAdded3);
        allEventRecords.add(eventRecordAdded4);
        allEventRecords.add(eventRecordAdded5);
        allEventRecords.add(eventRecordAdded6);
        allEventRecords.add(eventRecordAdded7);

        return allEventRecords.getTotalCount();
    }
}