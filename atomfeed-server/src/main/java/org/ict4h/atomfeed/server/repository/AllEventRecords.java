package org.ict4h.atomfeed.server.repository;

import java.util.List;

import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;

/**
 * The interface {@code AllEventRecords} contains methods to perform {@code EventRecord} retrieval and
 * addition.
 */
public interface AllEventRecords {

    /**
     * Adds an {@code EventRecord} to the underlying data store
     * @param eventRecord an {@code EventRecord} to be created
     * @throws AtomFeedRuntimeException when creation of an {@code EventRecord} is not successful
     */
	void add(EventRecord eventRecord);

    /**
     * Fetches an {@code EventRecord} that is identified by an unique {@code UUID}
     * @param uuid that uniquely identifies an {@code EventRecord}
     * @return <p>An {@code EventRecord} identified by the {@code UUID} identifier.</p>
     *         If an {@code EventRecord} cannot be found for the given identifier, the result is null.
     * @throws AtomFeedRuntimeException
     */
	EventRecord get(String uuid);

    /**
     * Retrieves the total count of {@code EventRecord} entities present in the underlying data store that are associated with {@code String} category.
     * @param category an {@code String} that refers to the category that a {@code EventRecord} is associated with.
     * @return An {@code Integer} count of the total number of {@code EventRecord} entities present.
     * @throws AtomFeedRuntimeException
     */
    int getTotalCountForCategory(String category);

    /**
     * Fetches a {@code List} of {@code EventRecord} from the underlying data store with size specified by {@code Integer} limit and starting point specified by {@code Integer} offset (exclusive), ordered by Identity.
     * @param category an {@code String} that refers to the category that an {@code EventRecord} is associated with.
     * @param offset an {@code Integer} that refers to the starting offset, exclusive.
     * @param limit an {@code Integer} that refers to the size of the {@code List} of {@code EventRecord} to be retrieved.
     * @return {@code List} of {@code EventRecord}
     * @throws AtomFeedRuntimeException
     */
    List<EventRecord> getEventsFromRangeForCategory(String category, Integer offset, Integer limit);

    /**
     * Fetches a {@code List} of {@code EventRecord} from the underlying data store that lie within a {@code TimeRange}.
     *
     * @param timeRange a {@code TimeRange} that specifies the time range.
     * @param category
     * @return {@code List} of {@code EventRecord}
     * @throws AtomFeedRuntimeException
     */
    List<EventRecord> getEventsFromTimeRange(TimeRange timeRange, String category);
}