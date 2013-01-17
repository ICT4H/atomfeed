package org.ict4htw.atomfeed.server.feed;

import java.util.List;

import org.ict4htw.atomfeed.server.domain.EventRecord;

public class EventFeed {

	private String feedId;
	private List<EventRecord> events;

	public EventFeed(String feedId, List<EventRecord> events) {
		this.feedId = feedId;
		this.events = events;
	}

	public String getId() {
		return feedId;
	}

	public List<EventRecord> getEvents() {
		return events;
	}

}
