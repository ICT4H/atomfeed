package org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

public class TimeBasedChunkingHistoryTest {
    @Test
    public void sequenceNumberWhenThereIsOnlyOneItemInHistory() {
        // duration in hours -- 2; now = 3; start=0 end=null
        Duration duration = Duration.standardHours(2);
        LocalDateTime now = LocalDateTime.now();
        TimeBasedChunkingHistoryEntry timeBasedChunkingHistoryEntry = new TimeBasedChunkingHistoryEntry(now.minusHours(3), now, duration);
        Assert.assertEquals(2, new TimeBasedChunkingHistory(timeBasedChunkingHistoryEntry).currentSequenceNumber());

        timeBasedChunkingHistoryEntry = new TimeBasedChunkingHistoryEntry(now.minusHours(1), now, duration);
        Assert.assertEquals(1, new TimeBasedChunkingHistory(timeBasedChunkingHistoryEntry).currentSequenceNumber());
    }

    @Test
    public void sequenceNumberWhenThereAreMultipleItemsInHistory() {
        // hours -- 0-2; 2-4; 4-7; 7-. now = 8
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfGame = now.minusHours(8);
        TimeBasedChunkingHistoryEntry timeBasedChunkingHistoryEntry1 = new TimeBasedChunkingHistoryEntry(startOfGame, startOfGame.plusHours(4), Duration.standardHours(2));
        TimeBasedChunkingHistoryEntry timeBasedChunkingHistoryEntry2 = new TimeBasedChunkingHistoryEntry(startOfGame.plusHours(4), null, Duration.standardHours(3));
        Assert.assertEquals(4, new TimeBasedChunkingHistory(timeBasedChunkingHistoryEntry1, timeBasedChunkingHistoryEntry2).currentSequenceNumber());
    }

    @Test
    public void timeRangeForASequenceNumber() {
        // hours -- 0-2; 2-4; 4-9; 9-. now = 10.
        LocalDateTime startOfGame = new LocalDateTime(2012, 1, 1, 0, 0, 0);
        TimeBasedChunkingHistoryEntry timeBasedChunkingHistoryEntry1 = new TimeBasedChunkingHistoryEntry(startOfGame, startOfGame.plusHours(4), Duration.standardHours(2));
        TimeBasedChunkingHistoryEntry timeBasedChunkingHistoryEntry2 = new TimeBasedChunkingHistoryEntry(startOfGame.plusHours(4), null, Duration.standardHours(5));
        TimeBasedChunkingHistory timeBasedChunkingHistory = new TimeBasedChunkingHistory(timeBasedChunkingHistoryEntry1, timeBasedChunkingHistoryEntry2);
        Assert.assertEquals(new TimeRange(startOfGame, startOfGame.plusHours(2)), timeBasedChunkingHistory.timeRangeFor(1));
        Assert.assertEquals(new TimeRange(startOfGame.plusHours(4), startOfGame.plusHours(9)), timeBasedChunkingHistory.timeRangeFor(3));
    }

    @Test
    public void timeRangeForWorkingSequence() {
        LocalDateTime startOfGame = LocalDateTime.now().minusHours(1);
        TimeBasedChunkingHistoryEntry entry = new TimeBasedChunkingHistoryEntry(startOfGame, null, Duration.standardHours(2));
        TimeBasedChunkingHistory history = new TimeBasedChunkingHistory(entry);
        long workingFeedId = history.currentSequenceNumber();
        Assert.assertEquals(new TimeRange(startOfGame, startOfGame.plusHours(2)), history.timeRangeFor((int) workingFeedId));
    }
}