package org.ict4h.atomfeed.server.domain;

import java.util.List;

public class EventFeed {
	private Integer feedId;
	private List<EventRecord> events;

	public EventFeed(Integer feedId, List<EventRecord> events) {
		this.feedId = feedId;
		this.events = events;
	}

	public Integer getId() {
		return feedId;
	}

	public List<EventRecord> getEvents() {
		return events;
	}
}
