package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.List;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberRange;
import org.ict4h.atomfeed.server.repository.AllEventRecords;

public class NumberFeedGenerator implements FeedGenerator {
	private AllEventRecords allEventRecords;
	private NumberBasedChunkingHistory numberBasedChunkingHistory;

	public NumberFeedGenerator(AllEventRecords eventsRecord, NumberBasedChunkingHistory numberBasedChunkingHistory) {
		this.allEventRecords = eventsRecord;
		this.numberBasedChunkingHistory = numberBasedChunkingHistory;
	}

	@Override
    public EventFeed getFeedForId(Integer feedId) {
		validateFeedId(feedId);
		return findFeed(feedId);	
	}
	
	@Override
    public EventFeed getRecentFeed() {
		int latestFeed = numberBasedChunkingHistory.getNumberOfFeeds(allEventRecords.getTotalCount());
		return findFeed(latestFeed);
	}

	private EventFeed findFeed(int feedId) {
		NumberRange feedRange = getFeedRange(feedId);
		List<EventRecord> events = allEventRecords.getEventsFromRange(feedRange.getFirst(), feedRange.getLast());
		return new EventFeed(feedId, events);
	}

	private NumberRange getFeedRange(Integer feedId) {
		return numberBasedChunkingHistory.findRange(feedId, allEventRecords.getTotalCount());
	}

	private void validateFeedId(Integer feedId) {
		if ( (feedId == null) || (feedId <= 0)  ) {
			throw new RuntimeException("feedId must not be null and must be greater than 0");
		}
		int numberOfFeeds = numberBasedChunkingHistory.getNumberOfFeeds(allEventRecords.getTotalCount());
		if (feedId > numberOfFeeds) {
			throw new RuntimeException("feed does not exist");
		}
	}
}
