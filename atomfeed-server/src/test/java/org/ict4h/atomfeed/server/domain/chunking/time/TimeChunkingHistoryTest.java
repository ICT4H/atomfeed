package org.ict4h.atomfeed.server.domain.chunking.time;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class TimeChunkingHistoryTest {
    @Test
    public void shouldGetTwoFeedSequenceNumbersWhenThereIsOnlyOneItemInHistory() {
        // duration in hours -- 2; now = 3; start=0 end=null
        Long interval = Duration.ofHours(2).toMillis();

        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();
        long startTime = LocalDateTime.now().minusHours(3).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        timeChunkingHistory.add(startTime,interval);
        Assert.assertEquals(2, timeChunkingHistory.currentSequenceNumber());
    }

    @Test
    public void shouldGetOnlyOneFeedWhenDurationExceedsTimeRangeWhenThereIsOnlyOneItemInHistory(){
        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();
        long startTime = LocalDateTime.now().minusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        timeChunkingHistory.add(startTime, Duration.ofHours(2).toMillis());
        assertEquals(1, timeChunkingHistory.currentSequenceNumber());
    }

    @Test
    public void sequenceNumberWhenThereAreMultipleItemsInHistory() {
        // hours -- 0-2; 2-4; 4-7; 7-. now = 8
        LocalDateTime startOfGame = LocalDateTime.now().minusHours(8);
        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();

        long startTime = startOfGame.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        timeChunkingHistory.add(startTime,Duration.ofHours(2).toMillis());

        startTime = startOfGame.plusHours(4).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        timeChunkingHistory.add(startTime,Duration.ofHours(3).toMillis());
        Assert.assertEquals(4, timeChunkingHistory.currentSequenceNumber());

    }

    @Test
    public void timeRangeForASequenceNumber() {
        // hours -- 0-2; 2-4; 4-9; 9-. now = 10.
        LocalDateTime startOfGame = LocalDateTime.of(2012,1,1,0,0,0);

        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();

        long startTime = startOfGame.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        timeChunkingHistory.add(startTime,Duration.ofHours(2).toMillis());
        startTime = startOfGame.plusHours(4).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        timeChunkingHistory.add(startTime,Duration.ofHours(5).toMillis());
        TimeRange expected = new TimeRange(startOfGame, startOfGame.plusHours(2));
        TimeRange actual = timeChunkingHistory.timeRangeFor(1);
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(new TimeRange(startOfGame.plusHours(4), startOfGame.plusHours(9)), timeChunkingHistory.timeRangeFor(3));
    }

    @Test
    public void timeRangeForWorkingSequence() {
        LocalDateTime startOfGame = LocalDateTime.now().minusHours(1);
        TimeChunkingHistory history = new TimeChunkingHistory();
        Long startTime = startOfGame.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        history.add(startTime,Duration.ofHours(2).toMillis());
        long workingFeedId = history.currentSequenceNumber();
        Assert.assertEquals(new TimeRange(startOfGame, startOfGame.plusHours(2)), history.timeRangeFor((int) workingFeedId));
    }
}