package org.ict4htw.atomfeed.server.service.feedgenerator;

import org.ict4htw.atomfeed.server.domain.EventFeed;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeBasedChunkingHistory;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeBasedChunkingHistoryEntry;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;

import java.util.List;

public class TimeFeedGenerator implements FeedGenerator {
    private final TimeBasedChunkingHistory timeBasedChunkingHistory;
    private final AllEventRecords allEventRecords;

    public TimeFeedGenerator(TimeBasedChunkingHistory timeBasedChunkingHistory, AllEventRecords allEventRecords) {
        this.timeBasedChunkingHistory = timeBasedChunkingHistory;
        this.allEventRecords = allEventRecords;
    }

    @Override
    public EventFeed getFeedForId(Integer feedId) {
        TimeRange timeRange = timeBasedChunkingHistory.timeRangeFor(feedId);
        List<EventRecord> eventRecords = allEventRecords.getEventsFromTimeRange(timeRange);
        return new EventFeed(feedId,eventRecords);
    }

    @Override
    public EventFeed getRecentFeed() {
        return getFeedForId(timeBasedChunkingHistory.getWorkingFeedId());
    }
}
