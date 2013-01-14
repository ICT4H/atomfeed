package org.ict4htw.atomfeed.server.repository;

import java.util.List;

import org.ict4htw.atomfeed.server.domain.EventRecord;

public interface AllEventRecords {

	public abstract void add(EventRecord eventRecord);

	public abstract EventRecord get(String uuid);

	public abstract int getTotalCount();

	public abstract List<EventRecord> getEventsFromNumber(int startNumber,
			int numberOfEvents);

	public abstract int getUnarchivedEventsCount();

	public abstract List<EventRecord> getUnarchivedEvents(int limit);

}