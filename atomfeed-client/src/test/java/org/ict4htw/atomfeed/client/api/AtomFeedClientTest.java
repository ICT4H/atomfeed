package org.ict4htw.atomfeed.client.api;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.AllMarkers;
import org.junit.Test;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AtomFeedClientTest {

    @Test
    public void findsNoUnprocessedEventsWhenTheMostRecentIsProcessed() throws Exception {
        URI feedUri = new URI("www.example.com/feed/working");

        Entry latest = new Entry();
        latest.setId("latest");
        Feed feed = getFeed(latest);

        FeedClient client = new AtomFeedClient(getAllFeeds(feedUri, feed), getSingleMarker(feedUri, latest.getId()));

        assertTrue(client.unprocessedEvents(feedUri).isEmpty());
    }

    @Test
    public void findsASingleUnprocessedEvent() throws Exception {
        URI feedUri = new URI("www.example.com/feed/working");

        Entry lastProcessed = new Entry();
        lastProcessed.setId("lastProcessed");

        Entry latest = new Entry();
        latest.setId("latest");

        Feed feed = getFeed(lastProcessed, latest);
        FeedClient client = new AtomFeedClient(getAllFeeds(feedUri, feed), getSingleMarker(feedUri, lastProcessed.getId()));

        assertEquals(latest.getId(), client.unprocessedEvents(feedUri).get(0).getId());
    }

    private Feed getFeed(Entry... entries) {
        ArrayList mutableEntries = new ArrayList();
        mutableEntries.addAll(Arrays.asList(entries));
        Feed feed = new Feed();
        feed.setEntries(mutableEntries);
        return feed;
    }

    private AllFeeds getAllFeeds(URI feedUri, Feed feed) {
        AllFeeds feeds = mock(AllFeeds.class);
        when(feeds.getFor(feedUri)).thenReturn(feed);
        return feeds;
    }

    private AllMarkers getSingleMarker(URI feedUri, String entryId) throws Exception {
        AllMarkers markers = mock(AllMarkers.class);
        when(markers.get(feedUri)).thenReturn(new Marker(feedUri, entryId));
        return markers;
    }
}
