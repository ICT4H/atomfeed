package org.ict4h.atomfeed.server.service.feedgenerator;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeChunkingHistory;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;

import java.util.List;

public class FeedGeneratorProxy implements FeedGenerator {
    private FeedGenerator feedGenerator;
    private final Object lockObject = new Object();

    private final String chunkingStrategy;
    private AllEventRecords allEventRecords;
    private final ChunkingEntries allChunkingEntries;

    public FeedGeneratorProxy(String chunkingStrategy, AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        this.chunkingStrategy = chunkingStrategy;
        this.allEventRecords = allEventRecords;
        this.allChunkingEntries = allChunkingEntries;
    }

    private FeedGenerator timeBasedFeedGenerator() {
        List<ChunkingHistoryEntry> allEntries = allChunkingEntries.all();
        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();
        for (ChunkingHistoryEntry entry : allEntries) {
            timeChunkingHistory.add(entry.getLeftBound(), entry.getInterval());
        }
        return new TimeFeedGenerator(timeChunkingHistory, allEventRecords);
    }

    private FeedGenerator numberBasedFeedGenerator() {
        NumberChunkingHistory numberBasedChunking = new NumberChunkingHistory();
        List<ChunkingHistoryEntry> allEntries = allChunkingEntries.all();
        for (ChunkingHistoryEntry entry : allEntries) {
            numberBasedChunking.add(entry.getSequenceNumber(), entry.getInterval().intValue(), entry.getLeftBound().intValue());
        }
        return new NumberFeedGenerator(allEventRecords, numberBasedChunking);
    }

    private void init() {
        if (feedGenerator == null) {
            synchronized (lockObject) {
                if (feedGenerator == null) {
                    if (FeedGeneratorFactory.NumberBasedChunkingStrategy.equals(chunkingStrategy)) {
                        feedGenerator = numberBasedFeedGenerator();
                    } else {
                        feedGenerator = timeBasedFeedGenerator();
                    }
                }
            }
        }
    }

    @Override
    public EventFeed getFeedForId(Integer feedId, String category) {
        init();
        return feedGenerator.getFeedForId(feedId, category);
    }

    @Override
    public EventFeed getRecentFeed(String category) {
        init();
        return feedGenerator.getRecentFeed(category);
    }
}