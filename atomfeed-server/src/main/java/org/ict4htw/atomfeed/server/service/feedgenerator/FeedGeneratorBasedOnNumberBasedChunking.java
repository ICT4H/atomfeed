package org.ict4htw.atomfeed.server.service.feedgenerator;

import java.util.List;

import org.ict4htw.atomfeed.server.domain.EventFeed;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry.Range;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class FeedGeneratorBasedOnNumberBasedChunking implements FeedGenerator {
	private AllEventRecords allEventRecords;
	private NumberBasedChunkingHistory numberBasedChunkingHistory;

    @Autowired
	public FeedGeneratorBasedOnNumberBasedChunking(AllEventRecords eventsRecord, NumberBasedChunkingHistory numberBasedChunkingHistory) {
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
		Range feedRange = getFeedRange(feedId);
		List<EventRecord> events = allEventRecords.getEventsFromRange(feedRange.first, feedRange.last);
		return new EventFeed(feedId, events);
	}

	private Range getFeedRange(Integer feedId) {
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
