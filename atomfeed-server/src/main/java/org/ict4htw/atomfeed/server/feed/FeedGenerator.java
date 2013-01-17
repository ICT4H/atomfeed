package org.ict4htw.atomfeed.server.feed;

import java.util.List;

import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.feed.ChunkingHistory.Range;

public class FeedGenerator {

	private AllEventRecords eventsRecord;
	private ChunkingHistory history;
	
	public FeedGenerator(AllEventRecords eventsRecord, ChunkingHistory history) {
		this.eventsRecord = eventsRecord;
		this.history = history;
	}

	public EventFeed getFeedForId(String feedId) {
		validateFeedId(feedId);
		Range feedRange = getFeedRange(feedId);
		List<EventRecord> events = eventsRecord.getEventsFromRange(feedRange.first, feedRange.last);
		return new EventFeed(feedId, events);
	}

	private Range getFeedRange(String feedId) {
		return history.findRange(Integer.valueOf(feedId), eventsRecord.getTotalCount());
	}

	private void validateFeedId(String feedId) {
		if ( (feedId == null) || ("".equals(feedId.trim()) )) {
			throw new RuntimeException("feedId must not be null or emptry String");
		}
		Integer.valueOf(feedId);
	}

}
