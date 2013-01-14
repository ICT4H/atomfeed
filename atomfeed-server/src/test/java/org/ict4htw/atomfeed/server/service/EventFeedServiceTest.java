package org.ict4htw.atomfeed.server.service;

import com.sun.syndication.feed.atom.Feed;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.repository.InMemoryEventRecordCreator;
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
        AllEventRecordsStub allEventRecords = new AllEventRecordsStub();
        InMemoryEventRecordCreator inMemoryEventRecordCreator = new InMemoryEventRecordCreator(allEventRecords);
        inMemoryEventRecordCreator.create();
        eventFeedService = new EventFeedService(allEventRecords);
    }

    @Test
    public void shouldGetRecentFeed() throws URISyntaxException {
        Feed feed = eventFeedService.getRecentFeed(new URI("http://hostname/events/recent"));
        System.out.println(Util.stringifyFeed(feed));
    }

    @Test
    public void shouldGetEventFeed() throws URISyntaxException {
        Feed feed = eventFeedService.getEventFeed(5, 9, new URI("http://hostname/events/5,10"));
        System.out.println(Util.stringifyFeed(feed));
    }
}
