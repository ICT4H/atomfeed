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


    private FeedEnumerator getEnumerator(URI uri) {
        Marker lastRead = allMarkers.get(uri);
        if (lastRead == null) {
            lastRead = new Marker(feedUri, null, null);
        }
        return new FeedEnumerator(allFeeds, lastRead);
    }


    private class FeedEntryProcessor implements AFTransactionWork {
        private Event eventInProcess;
        private Feed currentFeed;

        public FeedEntryProcessor(Event eventInProcess, Feed currentFeed) {
            this.eventInProcess = eventInProcess;
            this.currentFeed = currentFeed;
        }

        @Override
        public void execute() {
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

    @Override
    public void processEvents() {
        logger.info(String.format("Processing events for feed URI : %s using event worker : %s", this.feedUri, eventWorker.getClass().getSimpleName()));
        try {
            transactionManager.executeWithTransaction(new AFTransactionWork() {
                private final URI uri = feedUri;
                @Override
                public PropagationDefinition getTxPropagationDefinition() {
                    return PropagationDefinition.PROPAGATION_REQUIRED;
                }
                @Override
                public void execute() {
                    final FeedEnumerator enumerator = getEnumerator(this.uri);
                    for (final Entry entry : enumerator) {
                        if (shouldNotProcessEvents(this.uri)) {
                            logger.warn("Too many failed events have failed while processing. Cannot continue.");
                            return;
                        }
                        Event eventInProcess = null;
                        try {
                            eventInProcess = new Event(entry, getEntryFeedUri(enumerator));
                            transactionManager.executeWithTransaction(new FeedEntryProcessor(eventInProcess, enumerator.getCurrentFeed()));
                        } catch (final Exception e) {
                            logger.error("ERROR occurred while processing feed entry", e);
                            final Event failedEvent = eventInProcess;
                            try {
                                transactionManager.executeWithTransaction(new AFTransactionWork() {
                                    @Override
                                    public void execute() {
                                        handleFailedEvent(entry, feedUri, e, enumerator.getCurrentFeed(), failedEvent);
                                    }
                                    @Override
                                    public PropagationDefinition getTxPropagationDefinition() {
                                        return PropagationDefinition.PROPAGATION_REQUIRES_NEW;
                                    }
                                });
                            } catch (Exception feEx) {
                                throw new RuntimeException(
                                        String.format("Error occurred while trying to save failed event. %s", failedEvent)
                                        , feEx);
                            }
                        } finally {
                            eventWorker.cleanUp(eventInProcess);
                        }
                    }
                }
            });
        } catch (Exception e) {
            throw new AtomFeedClientException(e);
        } finally {
            //?
        }
    }

    private class FailedFeedEventProcessor implements AFTransactionWork {
        private final FailedEvent eventInProcess;
        public FailedFeedEventProcessor(FailedEvent failedEvent) {
            this.eventInProcess = failedEvent;
        }
        @Override
        public void execute() {
            logger.debug(String.format("Processing failed event : %s", eventInProcess));
            eventWorker.process(eventInProcess.getEvent());
            allFailedEvents.remove(eventInProcess);
        }

        @Override
        public PropagationDefinition getTxPropagationDefinition() {
            return PropagationDefinition.PROPAGATION_REQUIRES_NEW;
        }
    }

    @Override
    public void processFailedEvents() {
        logger.info(String.format("Processing failed events for feed URI : %s using event worker : %s",
                feedUri, eventWorker.getClass().getSimpleName()));
        try {
            transactionManager.executeWithTransaction(new AFTransactionWork() {
                @Override
                public void execute() {
                    List<FailedEvent> failedEvents = allFailedEvents.getOldestNFailedEvents(feedUri.toString(), FAILED_EVENTS_PROCESS_BATCH_SIZE);
                    boolean hasProcessedEvent = false;
                    for (final FailedEvent failedEvent : failedEvents) {
                        try {
                            transactionManager.executeWithTransaction(new FailedFeedEventProcessor(failedEvent));
                            hasProcessedEvent = true;
                        } catch (final Exception e) {
                            logger.info(String.format("Failed to process failed event. %s", failedEvent), e);
                            try {
                                transactionManager.executeWithTransaction(new AFTransactionWork() {
                                    @Override
                                    public void execute() {
                                        updateFailedEvents(failedEvent, e);
                                    }

                                    @Override
                                    public PropagationDefinition getTxPropagationDefinition() {
                                        return PropagationDefinition.PROPAGATION_REQUIRES_NEW;
                                    }
                                });
                            } catch (Exception fePEx) {
                                throw new RuntimeException(
                                        String.format("Error occurred while trying to update failed event. %s", failedEvent)
                                        , fePEx);
                            }

                        }
                    }
                }

                @Override
                public PropagationDefinition getTxPropagationDefinition() {
                    return PropagationDefinition.PROPAGATION_REQUIRED;
                }
            });
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

    private boolean shouldNotProcessEvents(URI feedUri) {
        return (allFailedEvents.getNumberOfFailedEvents(feedUri.toString()) >= atomFeedProperties.getMaxFailedEvents());
    }

    private void handleFailedEvent(Entry entry, URI feedUri, Exception e, Feed feed, Event event) {
        allFailedEvents.addOrUpdate(new FailedEvent(feedUri.toString(), event, Util.getExceptionString(e)));
        if (atomFeedProperties.controlsEventProcessing())
            allMarkers.put(this.feedUri, entry.getId(), Util.getViaLink(feed));
    }

}