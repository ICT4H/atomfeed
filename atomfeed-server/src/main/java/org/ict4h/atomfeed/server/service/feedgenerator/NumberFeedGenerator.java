package org.ict4h.atomfeed.server.service.feedgenerator;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberRange;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;

import java.util.ArrayList;
import java.util.List;

public class NumberFeedGenerator implements FeedGenerator {
	private AllEventRecords allEventRecords;
	private NumberChunkingHistory numberChunkingHistory;

	public NumberFeedGenerator(AllEventRecords eventsRecord, NumberChunkingHistory numberChunkingHistory) {
		this.allEventRecords = eventsRecord;
		this.numberChunkingHistory = numberChunkingHistory;
	}

	@Override
    public EventFeed getFeedForId(Integer feedId, String category) {
        int totalCountForCategory = allEventRecords.getTotalCountForCategory(category);
        validateFeedId(feedId, totalCountForCategory);
        return findFeed(feedId,category, totalCountForCategory);
	}
	
	@Override
    public EventFeed getRecentFeed(String category) {
        int totalCountForCategory = allEventRecords.getTotalCountForCategory(category);
        int latestFeed = numberChunkingHistory.getNumberOfFeeds(totalCountForCategory);
        if(isFeedZeroWithoutAnyEvents(latestFeed)){
            return new EventFeed(0,new ArrayList<EventRecord>());
        }
		return findFeed(latestFeed,category, totalCountForCategory);
	}

    private boolean isFeedZeroWithoutAnyEvents(int latestFeed) {
        return latestFeed == 0;
    }

    private EventFeed findFeed(int feedId, String category, int totalCountForCategory) {
        NumberRange feedRange = numberChunkingHistory.findRange(feedId, totalCountForCategory);
		List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(category, feedRange.getOffset(), feedRange.getLimit());
		return new EventFeed(feedId, events);
	}

    private void validateFeedId(Integer feedId, int totalCountForCategory) {
		if ( (feedId == null) || (feedId <= 0)  ) {
			throw new AtomFeedRuntimeException("feedId must not be null and must be greater than 0");
		}
		int numberOfFeeds = numberChunkingHistory.getNumberOfFeeds(totalCountForCategory);
		if (feedId > numberOfFeeds) {
			throw new AtomFeedRuntimeException("feed does not exist");
		}
	}
}
