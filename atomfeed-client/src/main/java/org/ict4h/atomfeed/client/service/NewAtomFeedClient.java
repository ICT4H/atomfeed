package org.ict4h.atomfeed.client.service;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.factory.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.util.Util;

import java.net.URI;
import java.util.Date;
import java.util.List;

public class NewAtomFeedClient implements FeedClient {
    private static final int FAILED_EVENTS_PROCESS_BATCH_SIZE = 5;

    private static Logger logger = Logger.getLogger(AtomFeedClient.class);

    private AllFeeds allFeeds;
    private AtomFeedProperties atomFeedProperties;

    private AFTransactionManager transactionManager;
    private URI feedUri;
    private EventWorker eventWorker;
    private AllMarkers allMarkers;
    private AllFailedEvents allFailedEvents;

    NewAtomFeedClient(AllFeeds allFeeds, AllMarkers allMarkers, AllFailedEvents allFailedEvents, URI feedUri, EventWorker eventWorker) {
        this(allFeeds, allMarkers, allFailedEvents, new AtomFeedProperties(), null, feedUri, eventWorker);
    }

    public NewAtomFeedClient(AllFeeds allFeeds, AllMarkers allMarkers, AllFailedEvents allFailedEvents, AtomFeedProperties atomFeedProperties,
                          AFTransactionManager transactionManager,
                          URI feedUri, EventWorker eventWorker) {
        this.allFeeds = allFeeds;
        this.allMarkers = allMarkers;
        this.allFailedEvents = allFailedEvents;
        this.atomFeedProperties = atomFeedProperties;
        this.transactionManager = transactionManager;
        this.feedUri = feedUri;
        this.eventWorker = eventWorker;
    }

    @Override
    public void processEvents() {
        logger.info(String.format("Processing events for feed URI : %s using event worker : %s", this.feedUri, eventWorker.getClass().getSimpleName()));
        try {
            Marker lastRead = transactionManager.executeWithTransaction(new MarkerReader(feedUri));
            final FeedEnumerator enumerator = new FeedEnumerator(allFeeds, lastRead);
            for (final Entry entry : enumerator) {
                Integer numberOfFailedEvents = transactionManager.executeWithTransaction(new FailedEventCounter(feedUri));
                if ((numberOfFailedEvents.intValue() >= atomFeedProperties.getMaxFailedEvents())) {
                    logger.error(String.format("Too many failed events for URI:%s have failed while processing. Cannot continue.", feedUri));
                    return;
                }
                Event eventInProcess = null;
                try {
                    eventInProcess = new Event(entry, getEntryFeedUri(enumerator));
                    logger.info(String.format("Processing event : %s", eventInProcess));
                    transactionManager.executeWithTransaction(new FeedEntryProcessor(eventInProcess, enumerator.getCurrentFeed()));
                } catch (final Exception eventProcessingException) {
                    logger.error(String.format("Error occurred while processing feed entry:%s", entry), eventProcessingException);
                    final Event failedEvent = eventInProcess;
                    try {
                        transactionManager.executeWithTransaction(new FailedEventHandler(feedUri, entry, failedEvent, enumerator.getCurrentFeed(), eventProcessingException));
                    } catch (Exception feEx) {
                        String errorMsg = String.format("Error occurred while trying to save event as Failed: %s", failedEvent);
                        logger.error(errorMsg, feEx);
                        throw new RuntimeException(errorMsg, feEx);
                    }
                } finally {
                    eventWorker.cleanUp(eventInProcess);
                }
            }
        } catch (Exception e) {
            throw new AtomFeedClientException(e);
        } finally {
            //?
        }
    }

    @Override
    public void processFailedEvents() {
        logger.info(String.format("Processing failed events for feed URI : %s using event worker : %s",
                feedUri, eventWorker.getClass().getSimpleName()));
        try {
            List<FailedEvent> failedEvents = transactionManager.executeWithTransaction(new FailedEventsFetcher());
            for (final FailedEvent failedEvent : failedEvents) {
                try {
                    logger.info(String.format("Processing previously failed event : %s", failedEvent));
                    transactionManager.executeWithTransaction(new FailedFeedEventProcessor(failedEvent));
                } catch (final Exception retryException) {
                    logger.error(String.format("Failed to process failed event. %s", failedEvent), retryException);
                    try {
                        transactionManager.executeWithTransaction(new FailedEventRetryFailureHandler(failedEvent, retryException));
                    } catch (Exception fePEx) {
                        String errorMsg = String.format("Error occurred while trying to update failed event. %s", failedEvent);
                        logger.error(errorMsg, fePEx);
                        throw new RuntimeException(errorMsg,fePEx);
                    }
                }
            }
        } catch (Exception e) {
            throw new AtomFeedClientException(e);
        } finally {
            //?
        }

    }


    private void updateFailedEvents(FailedEvent failedEvent, Exception e)  {
        failedEvent.setFailedAt(new Date().getTime());
        failedEvent.setErrorMessage(Util.getExceptionString(e));
        allFailedEvents.addOrUpdate(failedEvent);
    }

    private String getEntryFeedUri(FeedEnumerator feedEnumerator) {
        return Util.getSelfLink(feedEnumerator.getCurrentFeed()).toString();
    }

    private void handleFailedEvent(Entry entry, URI feedUri, Exception e, Feed feed, Event event) {
        allFailedEvents.addOrUpdate(new FailedEvent(feedUri.toString(), event, Util.getExceptionString(e)));
        if (atomFeedProperties.controlsEventProcessing())
            allMarkers.put(this.feedUri, entry.getId(), Util.getViaLink(feed));
    }

    private class FailedEventCounter implements AFTransactionWork<Integer> {
        private URI feedURI;
        public FailedEventCounter(URI feedURI) {
            this.feedURI = feedURI;
        }
        @Override
        public Integer execute() {
            return allFailedEvents.getNumberOfFailedEvents(this.feedURI.toString());
        }
        @Override
        public PropagationDefinition getTxPropagationDefinition() {
            return PropagationDefinition.PROPAGATION_REQUIRED;
        }
    }

    private class FeedEntryProcessor extends AFTransactionWorkWithoutResult {
        private Event eventInProcess;
        private Feed currentFeed;
        public FeedEntryProcessor(Event eventInProcess, Feed currentFeed) {
            this.eventInProcess = eventInProcess;
            this.currentFeed = currentFeed;
        }
        @Override
        protected void doInTransaction() {
            logger.debug("Processing event : " + this.eventInProcess);
            eventWorker.process(this.eventInProcess);
            if (atomFeedProperties.controlsEventProcessing()) {
                allMarkers.put(feedUri, this.eventInProcess.getId(), Util.getViaLink(this.currentFeed));
            }
        }
        @Override
        public PropagationDefinition getTxPropagationDefinition() {
            return PropagationDefinition.PROPAGATION_REQUIRES_NEW;
        }
    }

    private class MarkerReader implements AFTransactionWork<Marker> {
        private URI feedUri;
        public MarkerReader(URI feedUri) {
            this.feedUri = feedUri;
        }
        @Override
        public Marker execute() {
            Marker lastRead = allMarkers.get(feedUri);
            if (lastRead == null) {
                lastRead = new Marker(feedUri, null, null);
            }
            return lastRead;
        }
        @Override
        public PropagationDefinition getTxPropagationDefinition() {
            return PropagationDefinition.PROPAGATION_REQUIRED;
        }
    }

    private class FailedEventHandler extends AFTransactionWorkWithoutResult {
        private URI feedURI;
        private Entry failedEntry;
        private Event failedEvent;
        private Feed workingFeed;
        private Exception failureException;

        public FailedEventHandler(URI feedURI, Entry failedEntry, Event failedEvent, Feed workingFeed, Exception failureException) {
            this.feedURI = feedURI;
            this.failedEntry = failedEntry;
            this.failedEvent = failedEvent;
            this.workingFeed = workingFeed;
            this.failureException = failureException;
        }
        @Override
        protected void doInTransaction() {
            handleFailedEvent(this.failedEntry, this.feedURI, this.failureException, this.workingFeed, this.failedEvent);
        }
        @Override
        public PropagationDefinition getTxPropagationDefinition() {
            return PropagationDefinition.PROPAGATION_REQUIRES_NEW;
        }
    }

    private class FailedEventRetryFailureHandler extends AFTransactionWorkWithoutResult {
        private FailedEvent failedEvent;
        private Exception failureException;
        public FailedEventRetryFailureHandler(FailedEvent failedEvent, Exception failureException) {
            this.failedEvent = failedEvent;
            this.failureException = failureException;
        }
        @Override
        public AFTransactionWork.PropagationDefinition getTxPropagationDefinition() {
            return AFTransactionWork.PropagationDefinition.PROPAGATION_REQUIRES_NEW;
        }
        @Override
        protected void doInTransaction() {
            updateFailedEvents(this.failedEvent, this.failureException);
        }
    }

    private class FailedFeedEventProcessor extends AFTransactionWorkWithoutResult {
        private final FailedEvent eventInProcess;
        public FailedFeedEventProcessor(FailedEvent failedEvent) {
            this.eventInProcess = failedEvent;
        }
        @Override
        public PropagationDefinition getTxPropagationDefinition() {
            return PropagationDefinition.PROPAGATION_REQUIRES_NEW;
        }
        @Override
        protected void doInTransaction() {
            logger.debug(String.format("Processing failed event : %s", eventInProcess));
            eventWorker.process(eventInProcess.getEvent());
            allFailedEvents.remove(eventInProcess);
        }
    }

    private class FailedEventsFetcher implements AFTransactionWork<List<FailedEvent>> {
        @Override
        public List<FailedEvent> execute() {
            return allFailedEvents.getOldestNFailedEvents(feedUri.toString(), FAILED_EVENTS_PROCESS_BATCH_SIZE);
        }
        @Override
        public PropagationDefinition getTxPropagationDefinition() {
            return PropagationDefinition.PROPAGATION_REQUIRED;
        }
    }

}