package org.ict4htw.atomfeed.server.service;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import junit.framework.Assert;

import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4htw.atomfeed.server.service.feedgenerator.FeedGenerator;
import org.ict4htw.atomfeed.server.service.feedgenerator.NumberFeedGenerator;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

public class EventFeedServiceTest {
    private EventFeedService eventFeedService;

    @Before
    public void setupEventRecords() throws URISyntaxException {
        AllEventRecordsStub allEventRecords = new AllEventRecordsStub();
        InMemoryEventRecordCreator inMemoryEventRecordCreator = new InMemoryEventRecordCreator(allEventRecords);
        inMemoryEventRecordCreator.create(7);
        
        NumberBasedChunkingHistory config = new NumberBasedChunkingHistory();
        config.add(1, 5, 1);
        FeedGenerator feedGenerator = new NumberFeedGenerator(allEventRecords, config);
        eventFeedService = new EventFeedService(feedGenerator);
    }

    @Test
    public void shouldGetRecentFeed() throws URISyntaxException {
        String recentUrl = "http://hostname/feedgenerator/recent";
        Feed feed = eventFeedService.getRecentFeed(new URI(recentUrl));
        HashMap<String, Link> links = getAllFeedLinks(feed);
        Assert.assertNull(links.get("next-archive"));
        Assert.assertNotNull(links.get("prev-archive"));
        Assert.assertEquals(recentUrl, links.get("self").getHref());
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
        String feedUrl = "http://hostname/feedgenerator/1";
        Feed feed = eventFeedService.getEventFeed(new URI(feedUrl), 1);
        HashMap<String, Link> links = getAllFeedLinks(feed);
        Assert.assertNotNull(links.get("next-archive"));
        Assert.assertNull(links.get("prev-archive"));
        Assert.assertEquals(feedUrl, links.get("self").getHref());
    }
}
