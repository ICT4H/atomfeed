package org.ict4htw.atomfeed.client.api;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import org.ict4htw.atomfeed.client.AtomFeedClientException;
import org.ict4htw.atomfeed.client.FeedEnumerator;
import org.ict4htw.atomfeed.client.api.data.Event;
import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.AllMarkers;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FeedClientImpl implements FeedClient {
    private static final String ATOM_MEDIA_TYPE = "application/atom+xml";
    private AllFeeds allFeeds;
    private AllMarkers allMarkers;

    @Autowired
    public FeedClientImpl(AllFeeds allFeeds, AllMarkers allMarkers) {
        this.allFeeds = allFeeds;
        this.allMarkers = allMarkers;
    }

    @Override
    public List<Event> unprocessedEvents(String consumerId, String url) {
        try {
            Marker marker = allMarkers.get(consumerId);
            FeedEnumerator feedEnumerator = new FeedEnumerator(allFeeds, new URI(url));
            List<Entry> entries = feedEnumerator.newerEntries(marker.getEntryId());
            ArrayList<Event> events = new ArrayList<>();
            for (Entry entry : entries) {
                events.add(new Event());
            }
            return events;
        } catch (URISyntaxException e) {
            throw new AtomFeedClientException(e);
        }
    }

    @Override
    public void confirmProcessed(String feedEntryId, String consumerId) {
        allMarkers.update(consumerId, feedEntryId);
    }
}