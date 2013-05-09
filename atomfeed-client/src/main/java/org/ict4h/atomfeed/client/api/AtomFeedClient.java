package org.ict4h.atomfeed.client.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.AtomFeedClientException;
import org.ict4h.atomfeed.client.FeedEnumerator;
import org.ict4h.atomfeed.client.api.data.Event;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;

import com.sun.syndication.feed.atom.Entry;

public class AtomFeedClient implements FeedClient {
    private static final String ATOM_MEDIA_TYPE = "application/atom+xml";
    private AllFeeds allFeeds;
    private AllMarkers allMarkers;

    private static Logger logger = Logger.getLogger(AtomFeedClient.class);

    public AtomFeedClient(AllFeeds allFeeds, AllMarkers allMarkers) {
        this.allFeeds = allFeeds;
        this.allMarkers = allMarkers;
    }

    @Override
    public void processEvents(URI feedUri, EventWorker eventWorker) {
        List<Entry> entries; ArrayList<Event> events;
        Marker marker = allMarkers.get(feedUri);
        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeeds, feedUri);
        try {
            entries = (marker != null)
                    ? feedEnumerator.newerEntries(marker.getEntryId()) : feedEnumerator.getAllEntries();
        } catch (URISyntaxException e) {
            throw new AtomFeedClientException(e);
        }

        for (Entry entry : entries){
            Event event = null;
            try {
                event = new Event(entry);
                eventWorker.process(event);
            } catch (Exception e) {
                handleFailedEvent(event, e);
            }
        }
    }

    private void handleFailedEvent(Event event, Exception e) {
        logger.info("Processing of event failed.", e);
    }
}