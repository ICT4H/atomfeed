package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.List;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;

public class TimeFeedGenerator implements FeedGenerator {
    private TimeChunkingHistory timeChunkingHistory;
    private final AllEventRecords allEventRecords;
    private ChunkingEntries chunkingEntries;
    private final Object lockObject = new Object();

    public TimeFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries chunkingEntries) {
        this.chunkingEntries = chunkingEntries;
        this.allEventRecords = allEventRecords;
    }

    @Override
    public EventFeed getFeedForId(Integer feedId, String category) {
        validateFeedId(feedId);
        return feedFor(feedId, category);
    }

    @Override
    public EventFeed getRecentFeed(String category) {
        return feedFor(getTimeChunkingHistory().getWorkingFeedId(), category);
    }

    private void validateFeedId(Integer feedId) {
        Integer upperLimit = getTimeChunkingHistory().getWorkingFeedId();
        if (feedId > upperLimit) {
            throw new AtomFeedRuntimeException(String.format("The sequence number:%d lies in future", feedId));
        }
    }

    private EventFeed feedFor(Integer feedId, String category) {
        TimeRange timeRange = getTimeChunkingHistory().timeRangeFor(feedId);
        List<EventRecord> eventRecords = allEventRecords.getEventsFromTimeRange(timeRange, category);
        return new EventFeed(feedId, eventRecords);
    }

    private TimeChunkingHistory getTimeChunkingHistory() {
        if (this.timeChunkingHistory == null) {
            synchronized (lockObject) {
                List<ChunkingHistoryEntry> allEntries = chunkingEntries.all();
                TimeChunkingHistory timebasedChunking = new TimeChunkingHistory();
                for (ChunkingHistoryEntry entry : allEntries) {
                    timebasedChunking.add(entry.getLeftBound(), entry.getInterval());
                }
                this.timeChunkingHistory = timebasedChunking;
            }
        }
        return this.timeChunkingHistory;
    }

}
