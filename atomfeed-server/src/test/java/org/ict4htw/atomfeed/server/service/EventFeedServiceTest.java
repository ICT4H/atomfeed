package org.ict4htw.atomfeed.server.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4htw.atomfeed.server.util.Util;
import org.junit.Before;
import org.junit.Test;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

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
        String recentUrl = "http://hostname/feed/recent";
		Feed feed = eventFeedService.getRecentFeed(new URI(recentUrl));
        HashMap<String, Link> links = getAllFeedLinks(feed);
        Assert.assertNull(links.get("next-archive"));
        Assert.assertNotNull(links.get("prev-archive"));
        Assert.assertEquals(recentUrl, links.get("self").getHref());
        //System.out.println(Util.stringifyFeed(feed));
    }

    private HashMap<String, Link> getAllFeedLinks(Feed feed) {
		HashMap<String, Link> hashMap = new HashMap<String, Link>();
		List<Link> alternateLinks = feed.getAlternateLinks();
		for (Link link : alternateLinks) {
			hashMap.put(link.getRel(), link);
		}
		return hashMap;
	}

	@Test
    public void shouldGetEventFeed() throws URISyntaxException {
        String feedUrl = "http://hostname/feed/1";
		Feed feed = eventFeedService.getEventFeed(new URI(feedUrl), 1);
        HashMap<String, Link> links = getAllFeedLinks(feed);
        Assert.assertNotNull(links.get("next-archive"));
        Assert.assertNull(links.get("prev-archive"));
        Assert.assertEquals(feedUrl, links.get("self").getHref());
        //System.out.println(Util.stringifyFeed(feed));
    }
    
    private void addEvents(int eventNumber) throws URISyntaxException {
		for (int i= 1; i <= eventNumber; i++) {
			String title = "Event" + i;
			//eventsRecord.add(new EventRecord(UUID.randomUUID().toString(), title, new URI("http://uri/"+title), null));
		}
	}
}
