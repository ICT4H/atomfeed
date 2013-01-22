package org.ict4htw.atomfeed.server.repository;

import java.util.List;

import org.ict4htw.atomfeed.server.domain.EventFeed;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;

public interface AllEventRecords {

	void add(EventRecord eventRecord);

	EventRecord get(String uuid);

	int getTotalCount();

    void save(List<EventRecord> eventRecords);
	
	List<EventRecord> getEventsFromRange(Integer first, Integer last);

    List<EventRecord> getEventsFromTimeRange(TimeRange timeRange);
}