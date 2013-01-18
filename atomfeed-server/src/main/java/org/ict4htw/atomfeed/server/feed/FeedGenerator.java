package org.ict4htw.atomfeed.server.feed;

import java.util.List;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.feed.ChunkingEntry.Range;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;

public class FeedGenerator {

	private AllEventRecords eventsRecord;
	private ChunkingHistory history;
	
	public FeedGenerator(AllEventRecords eventsRecord, ChunkingHistory history) {
		this.eventsRecord = eventsRecord;
		this.history = history;
	}

	public EventFeed getFeedForId(Integer feedId) {
		validateFeedId(feedId);
		return findFeed(feedId);	
	}
	
	public EventFeed getRecentFeed() {
		int latestFeed = history.getNumberOfFeeds(eventsRecord.getTotalCount());
		return findFeed(latestFeed);
	}

	private EventFeed findFeed(int feedId) {
		Range feedRange = getFeedRange(feedId);
		List<EventRecord> events = eventsRecord.getEventsFromRange(feedRange.first, feedRange.last);
		return new EventFeed(feedId, events);
	}

	private Range getFeedRange(Integer feedId) {
		return history.findRange(feedId, eventsRecord.getTotalCount());
	}

	private void validateFeedId(Integer feedId) {
		if ( (feedId == null) || (feedId <= 0)  ) {
			throw new RuntimeException("feedId must not be null and must be greater than 0");
		}
		int numberOfFeeds = history.getNumberOfFeeds(eventsRecord.getTotalCount());
		if (feedId > numberOfFeeds) {
			throw new RuntimeException("feed does not exist");
		}
	}

	

}
