package org.ict4h.atomfeed.server.repository;

import java.util.ArrayList;
import java.util.List;

import org.ict4h.atomfeed.server.domain.EventRecordQueueItem;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;

/**
 * The interface {@code AllEventRecordsQueue} contains methods to perform {@code EventRecordQueueItem} retrieval and
 * addition.
 */
public interface AllEventRecordsQueue {

    /**
     * Adds an {@code EventRecordQueueItem} to the underlying data store
     * @param eventRecordQueueItem an {@code EventRecordQueueItem} to be created
     * @throws AtomFeedRuntimeException when creation of an {@code EventRecordQueueItem} is not successful
     */
    void add(EventRecordQueueItem eventRecordQueueItem);

    /**
     * Fetches an {@code EventRecordQueueItem} that is identified by an unique {@code UUID}
     * @param uuid that uniquely identifies an {@code EventRecordQueueItem}
     * @return <p>An {@code EventRecordQueueItem} identified by the {@code UUID} identifier.</p>
     *         If an {@code EventRecordQueueItem} cannot be found for the given identifier, the result is null.
     * @throws AtomFeedRuntimeException
     */
    EventRecordQueueItem get(String uuid);


    List<EventRecordQueueItem> getAll();

    void delete(String uuid);
}