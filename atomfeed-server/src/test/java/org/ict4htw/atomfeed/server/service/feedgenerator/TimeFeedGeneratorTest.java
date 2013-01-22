package org.ict4htw.atomfeed.server.service.feedgenerator;

import java.net.URI;

import junit.framework.Assert;

import org.ict4htw.atomfeed.server.domain.EventFeed;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeBasedChunkingHistoryStub;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.joda.time.LocalDateTime;
import org.junit.Test;


public class TimeFeedGeneratorTest {
    @Test
    public void shouldGetWorkingFeed() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);

        TimeBasedChunkingHistoryStub history = new TimeBasedChunkingHistoryStub(4);
        history.setTimeRange(4, new TimeRange(startTime,startTime.plusHours(2)));

        AllEventRecordsStub allEventRecordsStub = new AllEventRecordsStub();
        EventRecord eventRecord = new EventRecord(null, null, new URI(""), null, startTime.plusHours(1).toDate());
        allEventRecordsStub.add(eventRecord);

        FeedGenerator generator = new TimeFeedGenerator(history,allEventRecordsStub);

        EventFeed recentFeed = generator.getRecentFeed();
        Assert.assertEquals(4, recentFeed.getId().intValue());
        Assert.assertSame(eventRecord, recentFeed.getEvents().get(0));
    }
}
