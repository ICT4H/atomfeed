package org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration;

import org.ict4htw.atomfeed.server.exceptions.AtomFeedRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeBasedChunkingHistory {
    private List<TimeBasedChunkingHistoryEntry> chunkingHistoryEntries;

    public TimeBasedChunkingHistory(TimeBasedChunkingHistoryEntry... chunkingHistoryEntries) {
        this.chunkingHistoryEntries = new ArrayList<TimeBasedChunkingHistoryEntry>();
        Collections.addAll(this.chunkingHistoryEntries, chunkingHistoryEntries);
    }

    public long currentSequenceNumber() {
        int totalNumberOfFeeds = 0;
        for (int i = 0; i < chunkingHistoryEntries.size(); i++) {
            totalNumberOfFeeds += chunkingHistoryEntries.get(i).numberOfFeeds();
        }

        return totalNumberOfFeeds + 1;
    }

    public TimeRange timeRangeFor(int sequenceNumber) {
        int totalNumberOfFeeds = 0;
        for (TimeBasedChunkingHistoryEntry timeBasedChunkingHistoryEntry : chunkingHistoryEntries) {
            int numberOfFeedsInCurrentConfigurationItem = timeBasedChunkingHistoryEntry.numberOfFeeds();
            if (sequenceNumber <= numberOfFeedsInCurrentConfigurationItem + totalNumberOfFeeds) return timeBasedChunkingHistoryEntry.getTimeRangeForChunk(sequenceNumber - totalNumberOfFeeds);
            totalNumberOfFeeds += numberOfFeedsInCurrentConfigurationItem;
        }
        throw new AtomFeedRuntimeException(String.format("The sequence number:%d lies in future", sequenceNumber));
    }
}