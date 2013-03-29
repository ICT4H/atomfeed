package org.ict4h.atomfeed.server.domain.chunking.time;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class TimeChunkingHistoryTest {
    @Test
    public void shouldGetTwoFeedSequenceNumbersWhenThereIsOnlyOneItemInHistory() {
        // duration in hours -- 2; now = 3; start=0 end=null
        Long interval = Duration.standardHours(2).getMillis();

        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();
        long startTime = LocalDateTime.now().minusHours(3).toDate().getTime();
        timeChunkingHistory.add(startTime,interval);
        Assert.assertEquals(2, timeChunkingHistory.currentSequenceNumber());
    }

    @Test
    public void shouldGetOnlyOneFeedWhenDurationExceedsTimeRangeWhenThereIsOnlyOneItemInHistory(){
        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();
        long duration = Duration.standardHours(2).getMillis();
        long startTime = LocalDateTime.now().minusHours(1).toDate().getTime();
        timeChunkingHistory.add(startTime,duration);
        assertEquals(1, timeChunkingHistory.currentSequenceNumber());
    }

    @Test
    public void sequenceNumberWhenThereAreMultipleItemsInHistory() {
        // hours -- 0-2; 2-4; 4-7; 7-. now = 8
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfGame = now.minusHours(8);
        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();
        timeChunkingHistory.add(startOfGame.toDate().getTime(),Duration.standardHours(2).getMillis());
        timeChunkingHistory.add(startOfGame.plusHours(4).toDate().getTime(),Duration.standardHours(3).getMillis());
        Assert.assertEquals(4, timeChunkingHistory.currentSequenceNumber());
    }

    @Test
    public void timeRangeForASequenceNumber() {
        // hours -- 0-2; 2-4; 4-9; 9-. now = 10.
        LocalDateTime startOfGame = new LocalDateTime(2012, 1, 1, 0, 0, 0);

        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();
        timeChunkingHistory.add(startOfGame.toDate().getTime(),Duration.standardHours(2).getMillis());
        timeChunkingHistory.add(startOfGame.plusHours(4).toDate().getTime(),Duration.standardHours(5).getMillis());
        TimeRange expected = new TimeRange(startOfGame, startOfGame.plusHours(2));
        TimeRange actual = timeChunkingHistory.timeRangeFor(1);
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(new TimeRange(startOfGame.plusHours(4), startOfGame.plusHours(9)), timeChunkingHistory.timeRangeFor(3));
    }

    @Test
    public void timeRangeForWorkingSequence() {
        LocalDateTime startOfGame = LocalDateTime.now().minusHours(1);
        TimeChunkingHistory history = new TimeChunkingHistory();
        history.add(startOfGame.toDate().getTime(),Duration.standardHours(2).getMillis());
        long workingFeedId = history.currentSequenceNumber();
        Assert.assertEquals(new TimeRange(startOfGame, startOfGame.plusHours(2)), history.timeRangeFor((int) workingFeedId));
    }
}