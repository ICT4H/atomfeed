package org.ict4htw.atomfeed.server.service;

import com.sun.syndication.feed.atom.*;
import junit.framework.Assert;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4htw.atomfeed.server.service.feedgenerator.FeedGenerator;
import org.ict4htw.atomfeed.server.service.feedgenerator.NumberFeedGenerator;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static junit.framework.Assert.assertEquals;

public class EventFeedServiceTest {
    private EventFeedService eventFeedService;
    private AllEventRecordsStub allEventRecords;
    private InMemoryEventRecordCreator recordCreator;

    @Before
    public void setupEventRecords() throws URISyntaxException {
        allEventRecords = new AllEventRecordsStub();
        recordCreator = new InMemoryEventRecordCreator(allEventRecords);
        recordCreator.create(7);
        
        NumberBasedChunkingHistory config = new NumberBasedChunkingHistory();
        config.add(1, 5, 1);
        FeedGenerator feedGenerator = new NumberFeedGenerator(allEventRecords, config);
        eventFeedService = new EventFeedServiceImpl(feedGenerator);
    }

    @Test
    public void shouldGetRecentFeed() throws URISyntaxException {
        String recentUrl = "http://hostname/feedgenerator/recent";
        Feed feed = eventFeedService.getRecentFeed(new URI(recentUrl));
        HashMap<String, Link> links = getAllFeedLinks(feed);
        Assert.assertNull(links.get("next-archive"));
        Assert.assertNotNull(links.get("prev-archive"));
        assertEquals(recentUrl, links.get("self").getHref());
    }

//  Should fail when we read author data from the database
    @Test
    public void shouldGetAnAuthorForAnEntry() throws URISyntaxException {
        String recentUrl = "http://hostname/feedgenerator/recent";
        Feed feed = eventFeedService.getRecentFeed(new URI(recentUrl));
        Entry entry = (Entry) feed.getEntries().get(0);
        assertEquals("OpenMRS", ((Person) entry.getAuthors().get(0)).getName());
    }

    @Test
    public void shouldSetUpdatedDateToTodayWhenNoRecordsCanBeFound() throws URISyntaxException {
        String recentUrl = "http://hostname/feedgenerator/recent";
        allEventRecords.clear();
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        Feed feed = eventFeedService.getRecentFeed(new URI(recentUrl));
        assertEquals(date.getTime(), feed.getUpdated());
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
        assertEquals(feedUrl, links.get("self").getHref());
    }

    @Test
    public void shouldGenerateTheSameUUIDForFeedsThatAreNotRecent() throws URISyntaxException {
        String feedUrl = "http://hostname/feedgenerator/1";
        Feed feed = eventFeedService.getEventFeed(new URI(feedUrl), 2);
        Feed theSameFeed = eventFeedService.getEventFeed(new URI(feedUrl),2);
        assertEquals(feed.getId(),theSameFeed.getId());
    }

    @Test
    public void shouldGetContentsFromFeedWrappedInCDATA() throws URISyntaxException {
        String recentUrl = "http://hostname/feedgenerator/1";
        Feed feed = eventFeedService.getRecentFeed(new URI(recentUrl));
        Entry entry = (Entry) feed.getEntries().get(0);
        String contents = ((Content)(entry.getContents().get(0))).getValue();
        Assert.assertTrue(contents.startsWith("<![CDATA["));
        Assert.assertTrue(contents.endsWith("]]>"));
    }

    @Test
    public void shouldNotWrapContentsInCDATAWhenContentsAreNotPresent() throws URISyntaxException {
        String recentUrl = "http://hostname/feedgenerator/1";
        allEventRecords.clear();
        recordCreator.create(new EventRecord("","", new URI(""),null,new Date()));
        Feed feed = eventFeedService.getRecentFeed(new URI(recentUrl));
        Entry entry = (Entry) feed.getEntries().get(0);
        Assert.assertNull(((Content)entry.getContents().get(0)).getValue());
    }
}
