package org.ict4htw.atomfeed.server.service;

import com.sun.syndication.feed.atom.Feed;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.ict4htw.atomfeed.server.util.Util;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class EventFeedServiceTest {
    private EventFeedService eventFeedService;

    @Before
    public void setupEventRecords() throws URISyntaxException {
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", DateTime.now(), new URI("http://uri"), "dadsas");
        EventRecord eventRecordAdded3 = new EventRecord("uuid3", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded4 = new EventRecord("uuid4", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded5 = new EventRecord("uuid5", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded6 = new EventRecord("uuid6", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded7 = new EventRecord("uuid7", "title", DateTime.now(), new URI("http://uri"), "asdasd");

        AllEventRecords allEventRecords = new AllEventRecordsStub();
        allEventRecords.add(eventRecordAdded1);
        allEventRecords.add(eventRecordAdded2);
        allEventRecords.add(eventRecordAdded3);
        allEventRecords.add(eventRecordAdded4);
        allEventRecords.add(eventRecordAdded5);
        allEventRecords.add(eventRecordAdded6);
        allEventRecords.add(eventRecordAdded7);

        eventFeedService = new EventFeedService(allEventRecords);
    }

    @Test
    public void shouldGetRecentFeed() throws URISyntaxException {
        Feed feed = eventFeedService.getRecentFeed(new URI("http://hostname/events/recent"));
        System.out.println(Util.stringifyFeed(feed));
    }

    @Test
    public void shouldGetEventFeed() throws URISyntaxException {
        Feed feed = eventFeedService.getEventFeed(5, 10, new URI("http://hostname/events/5,10"));
        System.out.println(Util.stringifyFeed(feed));
    }
}
