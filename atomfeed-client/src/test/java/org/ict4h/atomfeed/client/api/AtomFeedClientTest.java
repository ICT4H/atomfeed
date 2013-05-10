package org.ict4h.atomfeed.client.api;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import org.ict4h.atomfeed.client.api.data.Event;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class AtomFeedClientTest {

    private String feedLink = "feedLink";

    @Test
    public void shouldCreateNewMarkerAndProcessWhenNoMarkerExists() throws URISyntaxException {
        URI feedUri = new URI("http://myFeedUri");
        AllFeeds allFeedsMock = mock(AllFeeds.class);
        AllMarkers allMarkersMock = mock(AllMarkers.class);
        final Entry entry = new Entry();
        entry.setId("id");
        Feed feed = getFeed(entry);
        entry.setSource(feed);
        when(allFeedsMock.getFor(feedUri)).thenReturn(feed);
        EventWorker eventWorker = mock(EventWorker.class);

        FeedClient feedClient = new AtomFeedClient(allFeedsMock, allMarkersMock);
        feedClient.processEvents(feedUri, eventWorker);

        verify(eventWorker).process(argThat(new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                return ((Event) o).getId().equals(entry.getId());
            }
        }));
        verify(allMarkersMock).processedTo(feedUri, entry.getId(), new URI(feedLink));
    }

    private Feed getFeed(Entry... entries) {
        ArrayList mutableEntries = new ArrayList();
        mutableEntries.addAll(Arrays.asList(entries));
        Feed feed = new Feed();
        feed.setAlternateLinks(Arrays.asList(new Link[]{getLink("self", feedLink)}));
        feed.setEntries(mutableEntries);
        return feed;
    }

    private Link getLink(String archiveType, String uri) {
        Link link = new Link();
        link.setRel(archiveType);
        link.setHref(uri);
        return link;
    }
}
