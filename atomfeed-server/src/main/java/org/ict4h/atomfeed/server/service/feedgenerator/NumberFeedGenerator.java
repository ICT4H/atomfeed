package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.ArrayList;
import java.util.List;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberRange;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
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
    public EventFeed getRecentFeed(String category) {
		int latestFeed = numberChunkingHistory.getNumberOfFeeds(allEventRecords.getTotalCountForCategory(category));
        if(isFeedZeroWithoutAnyEvents(latestFeed)){
            return new EventFeed(0,new ArrayList<EventRecord>());
        }
		return findFeed(latestFeed,category);
	}

    private boolean isFeedZeroWithoutAnyEvents(int latestFeed) {
        return latestFeed == 0;
    }

    private EventFeed findFeed(int feedId, String category) {
        NumberRange feedRange = numberChunkingHistory.findRange(feedId, allEventRecords.getTotalCountForCategory(category));
		List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(category, feedRange.getOffset(), feedRange.getLimit());
		return new EventFeed(feedId, events);
	}

    private void validateFeedId(Integer feedId, String category) {
		if ( (feedId == null) || (feedId <= 0)  ) {
			throw new AtomFeedRuntimeException("feedId must not be null and must be greater than 0");
		}
		int numberOfFeeds = numberChunkingHistory.getNumberOfFeeds(allEventRecords.getTotalCountForCategory(category));
		if (feedId > numberOfFeeds) {
			throw new AtomFeedRuntimeException("feed does not exist");
		}
	}
}
