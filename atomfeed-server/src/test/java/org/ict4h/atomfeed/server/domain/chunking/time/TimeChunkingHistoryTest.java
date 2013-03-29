package org.ict4h.atomfeed.server.domain.chunking.time;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TimeChunkingHistoryTest {
    @Test
    public void sequenceNumberWhenThereIsOnlyOneItemInHistory() {
        // duration in hours -- 2; now = 3; start=0 end=null
        Duration duration = Duration.standardHours(2);
        LocalDateTime now = LocalDateTime.now();
        TimeChunkingHistoryEntry timeChunkingHistoryEntry = new TimeChunkingHistoryEntry(now.minusHours(3), now, duration);
        Assert.assertEquals(2, new TimeChunkingHistory(Arrays.asList(timeChunkingHistoryEntry)).currentSequenceNumber());

        timeChunkingHistoryEntry = new TimeChunkingHistoryEntry(now.minusHours(1), now, duration);
        Assert.assertEquals(1, new TimeChunkingHistory(Arrays.asList(timeChunkingHistoryEntry)).currentSequenceNumber());
    }

    @Test
    public void sequenceNumberWhenThereAreMultipleItemsInHistory() {
        // hours -- 0-2; 2-4; 4-7; 7-. now = 8
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfGame = now.minusHours(8);
        TimeChunkingHistoryEntry timeChunkingHistoryEntry1 = new TimeChunkingHistoryEntry(startOfGame, startOfGame.plusHours(4), Duration.standardHours(2));
        TimeChunkingHistoryEntry timeChunkingHistoryEntry2 = new TimeChunkingHistoryEntry(startOfGame.plusHours(4), null, Duration.standardHours(3));
        Assert.assertEquals(4, new TimeChunkingHistory(Arrays.asList(timeChunkingHistoryEntry1, timeChunkingHistoryEntry2)).currentSequenceNumber());
    }

    @Test
    public void timeRangeForASequenceNumber() {
        // hours -- 0-2; 2-4; 4-9; 9-. now = 10.
        LocalDateTime startOfGame = new LocalDateTime(2012, 1, 1, 0, 0, 0);
        TimeChunkingHistoryEntry timeChunkingHistoryEntry1 = new TimeChunkingHistoryEntry(startOfGame, startOfGame.plusHours(4), Duration.standardHours(2));
        TimeChunkingHistoryEntry timeChunkingHistoryEntry2 = new TimeChunkingHistoryEntry(startOfGame.plusHours(4), null, Duration.standardHours(5));
        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory(Arrays.asList(timeChunkingHistoryEntry1, timeChunkingHistoryEntry2));
        TimeRange expected = new TimeRange(startOfGame, startOfGame.plusHours(2));
        TimeRange actual = timeChunkingHistory.timeRangeFor(1);
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(new TimeRange(startOfGame.plusHours(4), startOfGame.plusHours(9)), timeChunkingHistory.timeRangeFor(3));
    }

    @Test
    public void timeRangeForWorkingSequence() {
        LocalDateTime startOfGame = LocalDateTime.now().minusHours(1);
        TimeChunkingHistoryEntry entry = new TimeChunkingHistoryEntry(startOfGame, null, Duration.standardHours(2));
        TimeChunkingHistory history = new TimeChunkingHistory(Arrays.asList(entry));
        long workingFeedId = history.currentSequenceNumber();
        Assert.assertEquals(new TimeRange(startOfGame, startOfGame.plusHours(2)), history.timeRangeFor((int) workingFeedId));
    }
}