package org.ict4h.atomfeed.client;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.service.FeedEnumerator;
import org.junit.Before;
import org.junit.Test;

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
    private AllFeeds allFeedsMock;
    private URI notificationsUri;
    private URI firstFeedUri;
    private URI secondFeedUri;
    private URI recentFeedUri;

    @Before
    public void setUp() throws URISyntaxException {
        allFeedsMock = mock(AllFeeds.class);
        Feed first = new Feed(),second = new Feed(),last = new Feed();

        first.setEntries(getEntries(1,5));
        second.setEntries(getEntries(6,10));
        last.setEntries(getEntries(11,13));

        notificationsUri = new URI("http://host/patients/notifications");
        recentFeedUri = new URI("http://host/patients/3");
        secondFeedUri = new URI("http://host/patients/2");
        firstFeedUri = new URI("http://host/patients/1");

        last.setOtherLinks(Arrays.asList(new Link[]{getLink("prev-archive", secondFeedUri)}));
        second.setOtherLinks(Arrays.asList(getLink("prev-archive", firstFeedUri), getLink("next-archive", recentFeedUri)));
        first.setOtherLinks(Arrays.asList(new Link[]{getLink("next-archive", secondFeedUri)}));

        when(allFeedsMock.getFor(notificationsUri)).thenReturn(last);
        when(allFeedsMock.getFor(recentFeedUri)).thenReturn(last);
        when(allFeedsMock.getFor(secondFeedUri)).thenReturn(second);
        when(allFeedsMock.getFor(firstFeedUri)).thenReturn(first);
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
        Marker marker = new Marker(notificationsUri, null, null);
        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeedsMock, marker);
        List<String> entryIds = getEntries(feedEnumerator);
        assertEquals(Arrays.asList("1,2,3,4,5,6,7,8,9,10,11,12,13".split(",")), entryIds);
    }

    @Test
    public void shouldWorkWhenAFeedReturnsNoEntries() {
        Marker marker = new Marker(notificationsUri, "3", firstFeedUri);
        Feed secondFeed = new Feed();
        secondFeed.setOtherLinks(Arrays.asList(new Link[]{getLink("next-archive", recentFeedUri)}));
        when(allFeedsMock.getFor(secondFeedUri)).thenReturn(secondFeed);
        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeedsMock, marker);
        List<String> entryIds = getEntries(feedEnumerator);

        assertEquals(Arrays.asList("4,5,11,12,13".split(",")), entryIds);
    }

    @Test
    public void shouldProcessFromLastProcessedEntryInFeed() {
        Marker marker = new Marker(notificationsUri, "3", firstFeedUri);

        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeedsMock, marker);
        List<String> entryIds = getEntries(feedEnumerator);

        assertEquals(Arrays.asList("4,5,6,7,8,9,10,11,12,13".split(",")), entryIds);
    }

    @Test
    public void shouldProcessWhenAllEntriesOfLastReadFeedHaveBeenProcessed() {
        Marker marker = new Marker(notificationsUri, "5", firstFeedUri);

        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeedsMock, marker);
        List<String> entryIds = getEntries(feedEnumerator);

        assertEquals(Arrays.asList("6,7,8,9,10,11,12,13".split(",")), entryIds);
    }

    @Test(expected = AtomFeedClientException.class)
    public void shouldThrowExceptionWhenLastReadIdNotPresent() throws URISyntaxException {
        Marker marker = new Marker(notificationsUri, "7", firstFeedUri);
        new FeedEnumerator(allFeedsMock, marker);
    }
}