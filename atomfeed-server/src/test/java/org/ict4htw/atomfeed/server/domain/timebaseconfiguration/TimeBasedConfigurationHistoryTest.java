package org.ict4htw.atomfeed.server.domain.timebaseconfiguration;

import org.ict4htw.atomfeed.server.domain.timebasedconfiguration.TimeBasedConfigurationHistory;
import org.ict4htw.atomfeed.server.domain.timebasedconfiguration.TimeBasedConfigurationItem;
import org.ict4htw.atomfeed.server.domain.timebasedconfiguration.TimeRange;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

public class TimeBasedConfigurationHistoryTest {
    @Test
    public void sequenceNumberWhenThereIsOnlyOneItemInHistory() {
        // duration in hours -- 2; now = 3; start=0 end=null
        Duration duration = Duration.standardHours(2);
        LocalDateTime now = LocalDateTime.now();
        TimeBasedConfigurationItem timeBasedConfigurationItem = new TimeBasedConfigurationItem(now.minusHours(3), now, duration);
        Assert.assertEquals(2, new TimeBasedConfigurationHistory(timeBasedConfigurationItem).currentSequenceNumber());

        timeBasedConfigurationItem = new TimeBasedConfigurationItem(now.minusHours(1), now, duration);
        Assert.assertEquals(1, new TimeBasedConfigurationHistory(timeBasedConfigurationItem).currentSequenceNumber());
    }

    @Test
    public void sequenceNumberWhenThereAreMultipleItemsInHistory() {
        // hours -- 0-2; 2-4; 4-7; 7-. now = 8
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfGame = now.minusHours(8);
        TimeBasedConfigurationItem timeBasedConfigurationItem1 = new TimeBasedConfigurationItem(startOfGame, startOfGame.plusHours(4), Duration.standardHours(2));
        TimeBasedConfigurationItem timeBasedConfigurationItem2 = new TimeBasedConfigurationItem(startOfGame.plusHours(4), null, Duration.standardHours(3));
        Assert.assertEquals(4, new TimeBasedConfigurationHistory(timeBasedConfigurationItem1, timeBasedConfigurationItem2).currentSequenceNumber());
    }

    @Test
    public void timeRangeForASequenceNumber() {
        // hours -- 0-2; 2-4; 4-9; 9-. now = 10.
        LocalDateTime startOfGame = new LocalDateTime(2012, 1, 1, 0, 0, 0);
        TimeBasedConfigurationItem timeBasedConfigurationItem1 = new TimeBasedConfigurationItem(startOfGame, startOfGame.plusHours(4), Duration.standardHours(2));
        TimeBasedConfigurationItem timeBasedConfigurationItem2 = new TimeBasedConfigurationItem(startOfGame.plusHours(4), null, Duration.standardHours(5));
        TimeBasedConfigurationHistory timeBasedConfigurationHistory = new TimeBasedConfigurationHistory(timeBasedConfigurationItem1, timeBasedConfigurationItem2);
        Assert.assertEquals(new TimeRange(startOfGame, startOfGame.plusHours(2)), timeBasedConfigurationHistory.timeRangeFor(1));
        Assert.assertEquals(new TimeRange(startOfGame.plusHours(4), startOfGame.plusHours(9)), timeBasedConfigurationHistory.timeRangeFor(3));
    }
}