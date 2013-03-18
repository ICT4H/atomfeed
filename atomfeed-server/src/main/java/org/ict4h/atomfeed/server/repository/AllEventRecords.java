package org.ict4h.atomfeed.server.repository;

import java.util.List;

import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;

public interface AllEventRecords {

	void add(EventRecord eventRecord);

	EventRecord get(String uuid);

	int getTotalCount();

	List<EventRecord> getEventsFromRange(Integer first, Integer last);

    List<EventRecord> getEventsFromTimeRange(TimeRange timeRange);
}