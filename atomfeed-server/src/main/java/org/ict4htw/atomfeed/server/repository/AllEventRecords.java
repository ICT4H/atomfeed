package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllEventRecords {

	void add(EventRecord eventRecord);

	EventRecord get(String uuid);

	int getTotalCount();

	List<EventRecord> getEventsFromRange(Integer first, Integer last);

    List<EventRecord> getEventsFromTimeRange(TimeRange timeRange);
}