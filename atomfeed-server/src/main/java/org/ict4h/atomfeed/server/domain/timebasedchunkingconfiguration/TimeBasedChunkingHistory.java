package org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.joda.time.LocalDateTime;

public class TimeBasedChunkingHistory {
    private List<TimeBasedChunkingHistoryEntry> chunkingHistoryEntries;

    public TimeBasedChunkingHistory(List<TimeBasedChunkingHistoryEntry> chunkingHistoryEntries) {
        this.chunkingHistoryEntries = chunkingHistoryEntries;
    }

    public long currentSequenceNumber() {
        int completedFeeds = 0;
        for (int i = 0; i < chunkingHistoryEntries.size(); i++) {
            if (chunkingHistoryEntries.get(i).isUnbounded()) {
                completedFeeds += chunkingHistoryEntries.get(i).numberOfFeedsUpTo(LocalDateTime.now());
            } else {
                completedFeeds += chunkingHistoryEntries.get(i).numberOfFeeds();
            }
        }
        return completedFeeds + 1;
    }

    public TimeRange timeRangeFor(int sequenceNumber) {
        int feedsSoFar = 0;
        for (TimeBasedChunkingHistoryEntry timeBasedChunkingHistoryEntry : chunkingHistoryEntries) {
            int relativeSequenceNumber = sequenceNumber - feedsSoFar;

            if (timeBasedChunkingHistoryEntry.isUnbounded()
             || sequenceNumber <= timeBasedChunkingHistoryEntry.numberOfFeeds() + feedsSoFar) {
                return timeBasedChunkingHistoryEntry.getTimeRangeForChunk(relativeSequenceNumber);
            }
            feedsSoFar += timeBasedChunkingHistoryEntry.numberOfFeeds();
        }
        throw new AtomFeedRuntimeException(String.format("The sequence number:%d lies in future", sequenceNumber));
    }

    public Integer getWorkingFeedId() {
        return new Long(currentSequenceNumber()).intValue();
    }
}