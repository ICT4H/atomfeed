package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.List;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberRange;
import org.ict4h.atomfeed.server.repository.AllEventRecords;

public class NumberFeedGenerator implements FeedGenerator {
	private AllEventRecords allEventRecords;
	private NumberChunkingHistory numberChunkingHistory;

	public NumberFeedGenerator(AllEventRecords eventsRecord, NumberChunkingHistory numberChunkingHistory) {
		this.allEventRecords = eventsRecord;
		this.numberChunkingHistory = numberChunkingHistory;
	}

	@Override
    public EventFeed getFeedForId(Integer feedId, String category) {
		validateFeedId(feedId,category);
		return findFeed(feedId,category);
	}
	
	@Override
    public EventFeed getRecentFeed() {
		int latestFeed = numberChunkingHistory.getNumberOfFeeds(allEventRecords.getTotalCount());
		return findFeed(latestFeed);
	}

	private EventFeed findFeed(int feedId, String category) {
		NumberRange feedRange = getFeedRange(feedId, category);
		List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(category, feedRange.getOffset(), feedRange.getLimit());
		return new EventFeed(feedId, events);
	}

    private EventFeed findFeed(int feedId) {
        NumberRange feedRange = getFeedRange(feedId);
        List<EventRecord> events = allEventRecords.getEventsFromRange(feedRange.getOffset(), feedRange.getOffset() + feedRange.getLimit());
        return new EventFeed(feedId, events);
    }

    private NumberRange getFeedRange(Integer feedId) {
        return numberChunkingHistory.findRange(feedId, allEventRecords.getTotalCount());
    }

    private NumberRange getFeedRange(Integer feedId, String category) {
		return numberChunkingHistory.findRange(feedId, allEventRecords.getTotalCountForCategory(category));
	}

	@Override
    public void validateFeedId(Integer feedId, String category) {
		if ( (feedId == null) || (feedId <= 0)  ) {
			throw new RuntimeException("feedId must not be null and must be greater than 0");
		}
		int numberOfFeeds = numberChunkingHistory.getNumberOfFeeds(allEventRecords.getTotalCountForCategory(category));
		if (feedId > numberOfFeeds) {
			throw new RuntimeException("feed does not exist");
		}
	}
}
