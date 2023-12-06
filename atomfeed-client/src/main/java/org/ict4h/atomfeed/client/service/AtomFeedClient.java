package org.ict4h.atomfeed.client.service;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.domain.FailedEventRetryLog;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.util.Util;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;

import java.net.URI;
import java.util.Date;
import java.util.List;

public class AtomFeedClient implements FeedClient {
    private static Logger logger = LoggerFactory.getLogger(AtomFeedClient.class);

    private AllFeeds allFeeds;
    private AtomFeedProperties atomFeedProperties;

    private AFTransactionManager transactionManager;
    private URI feedUri;
    private EventWorker eventWorker;
    private AllMarkers allMarkers;
    private AllFailedEvents allFailedEvents;

    AtomFeedClient(AllFeeds allFeeds, AllMarkers allMarkers, AllFailedEvents allFailedEvents, URI feedUri, EventWorker eventWorker) {
        this(allFeeds, allMarkers, allFailedEvents, new AtomFeedProperties(), null, feedUri, eventWorker);
    }

    public AtomFeedClient(AllFeeds allFeeds, AllMarkers allMarkers, AllFailedEvents allFailedEvents, AtomFeedProperties atomFeedProperties,
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
                    transactionManager.executeWithTransaction(new EventProcessor(eventInProcess, enumerator.getCurrentFeed()));
                } catch (final Exception eventProcessingException) {
                    logger.error(String.format("Error occurred while processing feed entry:%s", entry), eventProcessingException);
                    try {
                        transactionManager.executeWithTransaction(new FailedEventHandler(feedUri, entry, eventInProcess, enumerator.getCurrentFeed(), eventProcessingException));
                    } catch (Exception feEx) {
                        String errorMsg = String.format("Error occurred while trying to save event as Failed: %s", eventInProcess);
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
                    transactionManager.executeWithTransaction(new FailedEventProcessor(failedEvent));
                } catch (final Exception retryException) {
                    logger.error(String.format("Failed to process failed event. %s", failedEvent), retryException);
                    try {
                        transactionManager.executeWithTransaction(new EventRetryFailureHandler(failedEvent, retryException));
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
        failedEvent.incrementRetryCount();
        allFailedEvents.addOrUpdate(failedEvent);

        long failedAt = new Date().getTime();
        String exceptionString = Util.getExceptionString(e);
        String eventContent = failedEvent.getEvent().getContent();
        FailedEventRetryLog failedEventRetryLog = new FailedEventRetryLog(failedEvent.getFeedUri(), failedAt, exceptionString, failedEvent.getEventId(), eventContent);
        allFailedEvents.insert(failedEventRetryLog);
    }

    private String getEntryFeedUri(FeedEnumerator feedEnumerator) {
        return Util.getSelfLink(feedEnumerator.getCurrentFeed()).toString();
    }

    private void handleFailedEvent(Entry entry, URI feedUri, Exception e, Feed feed, Event event) {
        final URI viaLink = Util.getViaLink(feed);
        final String errorMessage = String.format("Failed processing event in feed [%s] \n", viaLink.toString()).concat(Util.getExceptionString(e));
        allFailedEvents.addOrUpdate(new FailedEvent(feedUri.toString(), event, errorMessage, 0));
        if (atomFeedProperties.controlsEventProcessing()) {
            allMarkers.put(this.feedUri, entry.getId(), viaLink);
        }
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

    private class EventProcessor extends AFTransactionWorkWithoutResult {
        private Event eventInProcess;
        private Feed currentFeed;
        public EventProcessor(Event eventInProcess, Feed currentFeed) {
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
        public AFTransactionWork.PropagationDefinition getTxPropagationDefinition() {
            return AFTransactionWork.PropagationDefinition.PROPAGATION_REQUIRES_NEW;
        }
    }

    public class MarkerReader implements AFTransactionWork<Marker> {
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

    class FailedEventHandler extends AFTransactionWorkWithoutResult {
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

    class EventRetryFailureHandler extends AFTransactionWorkWithoutResult {
        private FailedEvent failedEvent;
        private Exception failureException;
        public EventRetryFailureHandler(FailedEvent failedEvent, Exception failureException) {
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

    class FailedEventProcessor extends AFTransactionWorkWithoutResult {
        private final FailedEvent eventInProcess;
        public FailedEventProcessor(FailedEvent failedEvent) {
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

    class FailedEventsFetcher implements AFTransactionWork<List<FailedEvent>> {
        @Override
        public List<FailedEvent> execute() {
            return allFailedEvents.getOldestNFailedEvents(feedUri.toString(), getProcessBatchSizeForFailedEvents(), atomFeedProperties.getFailedEventMaxRetry());
        }
        @Override
        public PropagationDefinition getTxPropagationDefinition() {
            return PropagationDefinition.PROPAGATION_REQUIRED;
        }
    }

    private int getProcessBatchSizeForFailedEvents() {
        return atomFeedProperties.getFailedEventsBatchProcessSize();
    }

}