package org.ict4h.atomfeed.server.domain.chunking.time;

import java.util.ArrayList;
import java.util.List;

import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

public class TimeChunkingHistory {
    private List<TimeChunkingHistoryEntry> chunkingHistoryEntries = new ArrayList<TimeChunkingHistoryEntry>();

    public TimeChunkingHistory(){}

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
        for (TimeChunkingHistoryEntry timeChunkingHistoryEntry : chunkingHistoryEntries) {
            int relativeSequenceNumber = sequenceNumber - feedsSoFar;

            if (timeChunkingHistoryEntry.isUnbounded()
             || sequenceNumber <= timeChunkingHistoryEntry.numberOfFeeds() + feedsSoFar) {
                return timeChunkingHistoryEntry.getTimeRangeForChunk(relativeSequenceNumber);
            }
            feedsSoFar += timeChunkingHistoryEntry.numberOfFeeds();
        }
        throw new AtomFeedRuntimeException(String.format("The sequence number:%d lies in future", sequenceNumber));
    }

    public Integer getWorkingFeedId() {
        return new Long(currentSequenceNumber()).intValue();
    }

    public void add(Long startTime, Long interval) {
        enforceRightBoundOnPreviousEntry(startTime);
        TimeChunkingHistoryEntry historyEntry = new TimeChunkingHistoryEntry(new LocalDateTime(startTime),null,new Duration(interval));
        chunkingHistoryEntries.add(historyEntry);
    }

    private void enforceRightBoundOnPreviousEntry(Long startTime) {
        int size = chunkingHistoryEntries.size();
        if(size == 0){
            return;
        }
        chunkingHistoryEntries.get(size - 1).enforceRightBound(startTime);
    }
}