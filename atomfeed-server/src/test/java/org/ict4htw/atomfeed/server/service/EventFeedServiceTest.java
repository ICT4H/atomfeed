package org.ict4htw.atomfeed.server.service;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

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
import java.util.List;

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
        List alternateLinks = feed.getAlternateLinks();
        for (Object link : alternateLinks) {
        	//System.out.println("link " + ((Link) link).getRel());
        	//TODO: verify that it has a via rel and prev-archive link
			
		}
        System.out.println(Util.stringifyFeed(feed));
    }

    @Test
    public void shouldGetEventFeed() throws URISyntaxException {
        Feed feed = eventFeedService.getEventFeed(5, 9, new URI("http://hostname/events/5,10"));
        System.out.println(Util.stringifyFeed(feed));
    }
}
