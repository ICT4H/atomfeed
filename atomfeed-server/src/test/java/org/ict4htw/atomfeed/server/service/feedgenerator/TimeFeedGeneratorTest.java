package org.ict4htw.atomfeed.server.service.feedgenerator;

import junit.framework.Assert;
import org.ict4htw.atomfeed.server.domain.EventFeed;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeBasedChunkingHistory;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeBasedChunkingHistoryEntry;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.net.URI;


public class TimeFeedGeneratorTest {
    @Test
    public void shouldGetWorkingFeed() throws Exception {
         Duration duration = Duration.standardHours(2);
        LocalDateTime now = LocalDateTime.now();

        TimeBasedChunkingHistoryEntry timeBasedChunkingHistoryEntry = new TimeBasedChunkingHistoryEntry(now.minusHours(3), null, duration);
        TimeBasedChunkingHistory timeBasedChunkingHistory = new TimeBasedChunkingHistory(timeBasedChunkingHistoryEntry);
        AllEventRecordsStub allEventRecordsStub = new AllEventRecordsStub();
        FeedGenerator generator = new TimeFeedGenerator(timeBasedChunkingHistory,allEventRecordsStub);

        EventRecord eventRecord = new EventRecord(null, null, new URI(""), null);
        allEventRecordsStub.add(eventRecord);
        EventFeed recentFeed = generator.getRecentFeed();

        Assert.assertEquals(2, recentFeed.getId().intValue());
    }

    @Test
    public void testGetRecentFeed() throws Exception {

    }
}
