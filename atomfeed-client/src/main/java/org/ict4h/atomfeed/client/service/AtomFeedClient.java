package org.ict4h.atomfeed.client.service;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.util.Util;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AtomFeedClient implements FeedClient {
    public static final int MAX_FAILED_EVENTS = 10;
    private static final int FAILED_EVENTS_PROCESS_BATCH_SIZE = 5;

    private static Logger logger = Logger.getLogger(AtomFeedClient.class);

    private AllFeeds allFeeds;
    private JdbcConnectionProvider jdbcConnectionProvider;
    private URI feedUri;
    private EventWorker eventWorker;
    private AllMarkers allMarkers;
    private AllFailedEvents allFailedEvents;
    private boolean updateMarker;

    public AtomFeedClient(AllFeeds allFeeds, AllMarkers allMarkers, AllFailedEvents allFailedEvents, URI feedUri, EventWorker eventWorker) {
        this(allFeeds, allMarkers, allFailedEvents, true, null, feedUri, eventWorker);
    }

    public AtomFeedClient(AllFeeds allFeeds, AllMarkers allMarkers, AllFailedEvents allFailedEvents,
                          boolean updateAtomFeedMarkerFlag, JdbcConnectionProvider jdbcConnectionProvider,
                          URI feedUri, EventWorker eventWorker) {
        this.allFeeds = allFeeds;
        this.allMarkers = allMarkers;
        this.allFailedEvents = allFailedEvents;
        this.updateMarker = updateAtomFeedMarkerFlag;
        this.jdbcConnectionProvider = jdbcConnectionProvider;
        this.feedUri = feedUri;
        this.eventWorker = eventWorker;
    }

    @Override
    public void processEvents() {
        logger.info(String.format("Processing events for feed URI : %s using event worker : %s", feedUri, eventWorker.getClass()));

        Connection connection = null;
        boolean autoCommit = false;
        try {
            connection = jdbcConnectionProvider.getConnection();
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            Marker marker = allMarkers.get(feedUri);
            if (marker == null) marker = new Marker(feedUri, null, null);
            FeedEnumerator feedEnumerator = new FeedEnumerator(allFeeds, marker);

            Event event = null;
            for (Entry entry : feedEnumerator) {
                if (shouldNotProcessEvents(feedUri)) {
                    logger.warn("Too many failed events have failed while processing. Cannot continue.");
                    return;
                }
                try {
                    event = new Event(entry, getEntryFeedUri(feedEnumerator));
                    logger.debug("Processing event : " + event);
                    eventWorker.process(event);
                    updateMarker(entry, feedEnumerator.getCurrentFeed());
                    connection.commit();
                } catch (Exception e) {
                    connection.rollback();
                    handleFailedEvent(entry, feedUri, e, feedEnumerator.getCurrentFeed(), event);
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            throw new AtomFeedClientException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(autoCommit);
                    connection.close();
                } catch (SQLException e) {
                    throw new AtomFeedClientException(e);
                }
            }
        }
    }

    private void updateMarker(Entry entry, Feed currentFeed) {
        if (updateMarker)
            allMarkers.put(feedUri, entry.getId(), Util.getViaLink(currentFeed));
    }


    @Override
    public void processFailedEvents() {
        logger.info(String.format("Processing failed events for feed URI : %s using event worker : %s", feedUri, eventWorker.getClass()));
        List<FailedEvent> failedEvents =
                allFailedEvents.getOldestNFailedEvents(feedUri.toString(), FAILED_EVENTS_PROCESS_BATCH_SIZE);

        for (FailedEvent failedEvent : failedEvents) {
            try {
                logger.debug(String.format("Processing failed event : %s", failedEvent));
                eventWorker.process(failedEvent.getEvent());
                allFailedEvents.remove(failedEvent);
            } catch (Exception e) {
                failedEvent.setFailedAt(new Date().getTime());
                failedEvent.setErrorMessage(Util.getExceptionString(e));
                allFailedEvents.add(failedEvent);
                logger.info(String.format("Failed to process failed event. %s", failedEvent));
            }
        }
    }

    private String getEntryFeedUri(FeedEnumerator feedEnumerator) {
        return Util.getSelfLink(feedEnumerator.getCurrentFeed()).toString();
    }

    private boolean shouldNotProcessEvents(URI feedUri) {
        return (allFailedEvents.getNumberOfFailedEvents(feedUri.toString()) >= MAX_FAILED_EVENTS);
    }

    private void handleFailedEvent(Entry entry, URI feedUri, Exception e, Feed feed, Event event) {
        allFailedEvents.add(new FailedEvent(feedUri.toString(), event, Util.getExceptionString(e)));
        updateMarker(entry, feed);
    }
}