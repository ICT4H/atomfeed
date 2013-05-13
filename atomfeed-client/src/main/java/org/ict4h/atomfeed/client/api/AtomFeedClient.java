package org.ict4h.atomfeed.client.api;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.AtomFeedClientException;
import org.ict4h.atomfeed.client.FeedEnumerator;
import org.ict4h.atomfeed.client.api.data.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;

import com.sun.syndication.feed.atom.Entry;
import org.ict4h.atomfeed.client.util.Util;

public class AtomFeedClient implements FeedClient {
    private static final String ATOM_MEDIA_TYPE = "application/atom+xml";

    private static final int MAX_FAILED_EVENTS = 10;
    private static final int FAILED_EVENTS_PROCESS_BATCH_SIZE = 5;

    private static Logger logger = Logger.getLogger(AtomFeedClient.class);

    private AllFeeds allFeeds;
    private AllMarkers allMarkers;
    private AllFailedEvents allFailedEvents;

    public AtomFeedClient(AllFeeds allFeeds, AllMarkers allMarkers, AllFailedEvents allFailedEvents) {
        this.allFeeds = allFeeds;
        this.allMarkers = allMarkers;
        this.allFailedEvents = allFailedEvents;
    }

    @Override
    public void processEvents(URI feedUri, EventWorker eventWorker) {
        if (shouldNotProcessEvents(feedUri))
            throw new AtomFeedClientException("Cannot start process events. Too many failed events.");

        Marker marker = allMarkers.get(feedUri);
        if(marker == null) marker = new Marker(feedUri, null, null);

        FeedEnumerator feedEnumerator = new FeedEnumerator(allFeeds, marker);

        Event event = null;
        for (Entry entry : feedEnumerator) {
            if (shouldNotProcessEvents(feedUri))
                throw new AtomFeedClientException("Too many failed events have failed while processing. Cannot continue.");

            try {
                event = new Event(entry);
                eventWorker.process(event);
                // Existing bug: If the call below starts failing and the call above passes, we shall
                // be in an inconsistent state.
                allMarkers.processedTo(feedUri, entry.getId(), Util.getSelfLink(entry.getSource()));
            } catch (Exception e) {
                handleFailedEvent(event, feedUri, e);
            }
        }
    }

    @Override
    public void processFailedEvents(URI feedUri, EventWorker eventWorker) {
        List<FailedEvent> failedEvents =
                allFailedEvents.getLastNFailedEvents(feedUri.toString(), FAILED_EVENTS_PROCESS_BATCH_SIZE);

        for (FailedEvent failedEvent : failedEvents) {
            try {
                eventWorker.process(failedEvent.getEvent());
                // Existing bug: If the call below starts failing and the call above passes, we shall
                // be in an inconsistent state.
                allFailedEvents.remove(failedEvent);
            } catch (Exception e) {
                logger.info("Failed to process failed event. " + failedEvent);
            }
        }
    }

    private boolean shouldNotProcessEvents(URI feedUri) {
        return (allFailedEvents.getNumberOfFailedEvents(feedUri.toString()) >= MAX_FAILED_EVENTS);
    }

    private void handleFailedEvent(Event event, URI feedUri, Exception e) {
        logger.info("Processing of event failed." + event, e);

        allFailedEvents.put(new FailedEvent(feedUri.toString(), event, Util.getExceptionString(e)));
    }
}