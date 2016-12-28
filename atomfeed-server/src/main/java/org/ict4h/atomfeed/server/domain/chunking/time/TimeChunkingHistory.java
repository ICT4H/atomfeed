package org.ict4h.atomfeed.server.domain.chunking.time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;

public class TimeChunkingHistory {
    private List<TimeChunkingHistoryEntry> chunkingHistoryEntries = new ArrayList<TimeChunkingHistoryEntry>();

    public TimeChunkingHistory() {
    }

    public long currentSequenceNumber() {
        return currentSequenceNumber(chunkingHistoryEntries.iterator());
    }

    private long currentSequenceNumber(Iterator<TimeChunkingHistoryEntry> iterator) {
        if (iterator.hasNext()) {
            return iterator.next().numberOfEncapsulatedFeeds() + currentSequenceNumber(iterator);
        }
        return 1;
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
        LocalDateTime start = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
        TimeChunkingHistoryEntry historyEntry = new TimeChunkingHistoryEntry( start, null, Duration.of(interval, ChronoUnit.MILLIS));
        chunkingHistoryEntries.add(historyEntry);
    }

    private void enforceRightBoundOnPreviousEntry(Long startTime) {
        int size = chunkingHistoryEntries.size();
        if (size == 0) {
            return;
        }
        chunkingHistoryEntries.get(size - 1).enforceRightBound(startTime);
    }
}