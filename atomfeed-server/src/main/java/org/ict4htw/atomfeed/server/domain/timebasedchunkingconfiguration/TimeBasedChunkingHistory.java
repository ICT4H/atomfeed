package org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration;

import org.ict4htw.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.joda.time.LocalDateTime;

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
}