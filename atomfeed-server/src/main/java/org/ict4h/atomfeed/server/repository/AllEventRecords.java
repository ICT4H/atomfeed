package org.ict4h.atomfeed.server.repository;

import java.util.List;

import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;

/**
 * The interface {@code AllEventRecords} contains methods to perform {@code EventRecord} retrieval and
 * addition.
 */
public interface AllEventRecords {

    /**
     * Adds an {@code EventRecord} to the underlying data store
     * @param eventRecord an {@code EventRecord} to be created
     * @throws RuntimeException when creation of an {@code EventRecord} is not successful
     */
	void add(EventRecord eventRecord);

    /**
     * Fetches an {@code EventRecord} that is identified by an unique {@code UUID}
     * @param uuid that uniquely identifies an {@code EventRecord}
     * @return <p>An {@code EventRecord} identified by the {@code UUID} identifier.</p>
     *         If an {@code EventRecord} cannot be found for the given identifier, the result is null.
     *
     */
	EventRecord get(String uuid);

    /**
     * Retrieves the total count of {@code EventRecord} entities present in the underlying data store.
     * @return An {@code Integer} count of the total number of {@code EventRecord} entities present.
     */
	int getTotalCount();

    /**
     * Fetches a {@code List} of {@code EventRecord} from the underlying data store that lie between an {@code Integer} range (inclusive), ordered by Identity.
     * @param first an {@code Integer} that refers to the Identity of an {@code EventRecord}, which denotes to the first {@code EventRecord} to retrieve.
     * @param last an {@code Integer} that refers to the Identity of an {@code EventRecord}, which denotes to the last {@code EventRecord} to retrieve.
     * @return {@code List} of {@code EventRecord}
     * @throws RuntimeException
     */
	List<EventRecord> getEventsFromRange(Integer first, Integer last);

    /**
     * Fetches a {@code List} of {@code EventRecord} from the underlying data store that lie within a {@code TimeRange}.
     * @param timeRange a {@code TimeRange} that specifies the time range.
     * @return {@code List} of {@code EventRecord}
     * @throws RuntimeException
     */
    List<EventRecord> getEventsFromTimeRange(TimeRange timeRange);
}