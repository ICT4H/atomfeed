package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.List;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.repository.AllEventRecords;

public class TimeFeedGenerator implements FeedGenerator {
    private final TimeChunkingHistory timeChunkingHistory;
    private final AllEventRecords allEventRecords;

    public TimeFeedGenerator(TimeChunkingHistory timeChunkingHistory, AllEventRecords allEventRecords) {
        this.timeChunkingHistory = timeChunkingHistory;
        this.allEventRecords = allEventRecords;
    }

    @Override
    public EventFeed getFeedForId(Integer feedId) {
        TimeRange timeRange = timeChunkingHistory.timeRangeFor(feedId);
        List<EventRecord> eventRecords = allEventRecords.getEventsFromTimeRange(timeRange);
        return new EventFeed(feedId,eventRecords);
    }

    @Override
    public EventFeed getRecentFeed() {
        return getFeedForId(timeChunkingHistory.getWorkingFeedId());
    }
}
