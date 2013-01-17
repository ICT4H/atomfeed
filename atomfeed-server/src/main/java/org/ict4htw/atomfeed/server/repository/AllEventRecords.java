package org.ict4htw.atomfeed.server.repository;

import java.util.List;

import org.ict4htw.atomfeed.server.domain.EventArchive;
import org.ict4htw.atomfeed.server.domain.EventRecord;

public interface AllEventRecords {

	void add(EventRecord eventRecord);

	EventRecord get(String uuid);

	int getTotalCount();

	List<EventRecord> getEventsFromNumber(int startNumber, int numberOfEvents);

	int getUnarchivedEventsCount();

	List<EventRecord> getUnarchivedEvents(int limit);

	void save(EventArchive eventArchive);

	EventArchive getLatestArchive();

	void save(List<EventRecord> eventRecords);

	EventArchive findArchiveById(String archiveId);

	List<EventRecord> getEventsFromRange(Integer first, Integer last);

}