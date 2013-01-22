package org.ict4htw.atomfeed.client;

import com.sun.syndication.feed.atom.Entry;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.datasource.WebClientStub;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4htw.atomfeed.server.resource.EventResource;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.ict4htw.atomfeed.server.service.EventService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class FeedEnumeratorTest {
    @Test @Ignore
    public void newerEntries() throws URISyntaxException {
        AllEventRecordsStub allEventRecords = new AllEventRecordsStub();
        EventService eventService = new EventService(allEventRecords);
        EventFeedService eventFeedService = new EventFeedService(allEventRecords);
        WebClientStub webClientStub = new WebClientStub(new EventResource(eventFeedService, eventService));

        InMemoryEventRecordCreator feedRecordCreator = new InMemoryEventRecordCreator(allEventRecords);
        feedRecordCreator.create(7);

        AllFeeds allFeeds = new AllFeeds(webClientStub);
        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeeds, new URI("http://foo.bar/baz"));
        List<Entry> entries = feedEnumerator.newerEntries(null);
        Assert.assertEquals(7, entries.size());
    }
}