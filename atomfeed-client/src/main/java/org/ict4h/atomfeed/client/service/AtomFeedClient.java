package org.ict4h.atomfeed.client.service;

import com.sun.syndication.feed.atom.Entry;
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
    private static final int MAX_FAILED_EVENTS = 10;
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
        logger.info("Processing events for feed URI : " + feedUri + " using event worker : " + eventWorker.getClass());

        Connection connection = null;
        try {
            connection = jdbcConnectionProvider.getConnection();
            connection.setAutoCommit(false);

            if (shouldNotProcessEvents(feedUri))
                throw new AtomFeedClientException("Cannot start process events. Too many failed events.");

            Marker marker = allMarkers.get(feedUri);
            if (marker == null) marker = new Marker(feedUri, null, null);

            FeedEnumerator feedEnumerator = new FeedEnumerator(allFeeds, marker);

            Event event = null;
            for (Entry entry : feedEnumerator) {
                try {
                    if (shouldNotProcessEvents(feedUri))
                        throw new AtomFeedClientException("Too many failed events have failed while processing. Cannot continue.");

                    try {
                        event = new Event(entry, getEntryFeedUri(feedEnumerator));
                        logger.debug("Processing event : " + event);

                        eventWorker.process(event);
                    } catch (Exception e) {
                        connection.rollback();
                        handleFailedEvent(event, feedUri, e);
                    }

                    // TODO : Mujir - this should be fixed now.
                    // Existing bug: If the call below starts failing and the call above passes, we shall
                    // be in an inconsistent state.
                    if (updateMarker)
                        allMarkers.put(feedUri, entry.getId(), Util.getViaLink(feedEnumerator.getCurrentFeed()));

                } catch(Exception e) {
                    connection.rollback();
                    // TODO : Mujir - is the atom feed processing sequential? Is it ok to rollback and continue processing others? Or should we altogether stop processing?
                } finally {
                    connection.commit();
                }
            }
        } catch(Exception e) {
            throw new AtomFeedClientException(e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e1) {
                throw new AtomFeedClientException(e1);
            }
        }
    }

    @Override
    public void processFailedEvents() {
        logger.info("Processing failed events for feed URI : " + feedUri + " using event worker : " + eventWorker.getClass());

        List<FailedEvent> failedEvents =
                allFailedEvents.getOldestNFailedEvents(feedUri.toString(), FAILED_EVENTS_PROCESS_BATCH_SIZE);

        for (FailedEvent failedEvent : failedEvents) {
            try {
                logger.debug("Processing failed event : " + failedEvent);

                eventWorker.process(failedEvent.getEvent());

                // Existing bug: If the call below starts failing and the call above passes, we shall
                // be in an inconsistent state.
                allFailedEvents.remove(failedEvent);
            } catch (Exception e) {
                failedEvent.setFailedAt(new Date().getTime());
                failedEvent.setErrorMessage(Util.getExceptionString(e));
                allFailedEvents.put(failedEvent);

                logger.info("Failed to process failed event. " + failedEvent);
            }
        }
    }

    private String getEntryFeedUri(FeedEnumerator feedEnumerator) {
        return Util.getSelfLink(feedEnumerator.getCurrentFeed()).toString();
    }

    private boolean shouldNotProcessEvents(URI feedUri) {
        return (allFailedEvents.getNumberOfFailedEvents(feedUri.toString()) >= MAX_FAILED_EVENTS);
    }

    private void handleFailedEvent(Event event, URI feedUri, Exception e) {
        logger.info("Processing of event failed." + event, e);
        allFailedEvents.put(new FailedEvent(feedUri.toString(), event, Util.getExceptionString(e)));
    }
}