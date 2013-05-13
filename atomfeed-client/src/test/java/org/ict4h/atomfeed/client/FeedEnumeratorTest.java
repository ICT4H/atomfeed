package org.ict4h.atomfeed.client;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.datasource.WebClientStub;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4h.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.EventFeedServiceImpl;
import org.ict4h.atomfeed.server.service.feedgenerator.FeedGenerator;
import org.ict4h.atomfeed.server.service.feedgenerator.NumberFeedGenerator;
import org.ict4h.atomfeed.spring.resource.EventResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.ExpectedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FeedEnumeratorTest {

    private InMemoryEventRecordCreator feedRecordCreator;
    private WebClientStub webClientStub;
    private AllFeeds allFeeds;

    private AllFeeds allFeedsMock;
    private URI recentFeedUri;
    private URI firstFeedUri;
    private Feed secondFeedMock;

    @Before
    public void setUp() throws URISyntaxException {
        allFeedsMock = mock(AllFeeds.class);
        Feed lastFeedMock = mock(Feed.class);
        secondFeedMock = mock(Feed.class);
        Feed firstFeedMock = mock(Feed.class);

        recentFeedUri = new URI("http://host/patients/notifications");
        URI thirdFeedUri = new URI("http://host/patients/3");
        URI secondFeedUri = new URI("http://host/patients/2");
        firstFeedUri = new URI("http://host/patients/1");

        when(allFeedsMock.getFor(recentFeedUri)).thenReturn(lastFeedMock);
        when(allFeedsMock.getFor(thirdFeedUri)).thenReturn(lastFeedMock);
        when(allFeedsMock.getFor(secondFeedUri)).thenReturn(secondFeedMock);
        when(allFeedsMock.getFor(firstFeedUri)).thenReturn(firstFeedMock);

        when(lastFeedMock.getAlternateLinks()).thenReturn(Arrays.asList(new Link[]{getLink("prev-archive", secondFeedUri)}));
        when(secondFeedMock.getAlternateLinks()).thenReturn(Arrays.asList(
                new Link[]{getLink("prev-archive", firstFeedUri), getLink("next-archive", thirdFeedUri)}));
        when(firstFeedMock.getAlternateLinks()).thenReturn(Arrays.asList(new Link[]{getLink("next-archive", secondFeedUri)}));

        when(firstFeedMock.getEntries()).thenReturn(getEntries(1, 5));
        when(secondFeedMock.getEntries()).thenReturn(getEntries(6, 10));
        when(lastFeedMock.getEntries()).thenReturn(getEntries(11, 13));

    }

    private FeedGenerator getFeedGenerator(AllEventRecords eventRecords) {
        NumberChunkingHistory config = new NumberChunkingHistory();
        config.add(1, 5, 1);
        return new NumberFeedGenerator(eventRecords, config);
    }

    private Link getLink(String archiveType, URI uri) {
        Link link = new Link();
        link.setRel(archiveType);
        link.setHref(uri.toString());
        return link;
    }

    private List<Entry> getEntries(int startNum, int endNum) {
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = startNum; i <= endNum; i++) {
            Entry entry = new Entry();
            entry.setId("" + i);
            entries.add(entry);
        }
        return entries;
    }

    private List<String> getEntries(FeedEnumerator feedEnumerator) {
        List<String> entryIds = new ArrayList<String>();
        for(Entry entry : feedEnumerator) {
            entryIds.add(entry.getId());
        }
        return entryIds;
    }

    @Test
    public void shouldCrawlBackToFirstFeedWhenNoMarkerPresent() throws URISyntaxException {
        Marker marker = new Marker(recentFeedUri, null, null);

        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeedsMock, marker);
        List<String> entryIds = getEntries(feedEnumerator);

        assertEquals(Arrays.asList("1,2,3,4,5,6,7,8,9,10,11,12,13".split(",")), entryIds);
    }

    @Test
    public void shouldWorkWhenAFeedReturnsNoEntries() {
        Marker marker = new Marker(recentFeedUri, "3", firstFeedUri);
        when(secondFeedMock.getEntries()).thenReturn(new ArrayList<Entry>());

        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeedsMock, marker);
        List<String> entryIds = getEntries(feedEnumerator);

        assertEquals(Arrays.asList("4,5,11,12,13".split(",")), entryIds);
    }

    @Test
    public void shouldProcessFromLastProcessedEntryInFeed() {
        Marker marker = new Marker(recentFeedUri, "3", firstFeedUri);

        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeedsMock, marker);
        List<String> entryIds = getEntries(feedEnumerator);

        assertEquals(Arrays.asList("4,5,6,7,8,9,10,11,12,13".split(",")), entryIds);
    }

    @Test
    public void shouldProcessWhenAllEntriesOfLastReadFeedHaveBeenProcessed() {
        Marker marker = new Marker(recentFeedUri, "5", firstFeedUri);

        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeedsMock, marker);
        List<String> entryIds = getEntries(feedEnumerator);

        assertEquals(Arrays.asList("6,7,8,9,10,11,12,13".split(",")), entryIds);
    }

    @Test(expected = AtomFeedClientException.class)
    public void shouldThrowExceptionWhenLastReadIdNotPresent() throws URISyntaxException {
        Marker marker = new Marker(recentFeedUri, "7", firstFeedUri);
        new FeedEnumerator(allFeedsMock, marker);
    }
}