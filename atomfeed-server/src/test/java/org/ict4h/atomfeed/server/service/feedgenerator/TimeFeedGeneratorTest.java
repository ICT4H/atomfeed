package org.ict4h.atomfeed.server.service.feedgenerator;

import java.net.URI;

import junit.framework.Assert;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeChunkingHistoryStub;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsStub;
import org.joda.time.LocalDateTime;
import org.junit.Test;


public class TimeFeedGeneratorTest {
    @Test
    public void shouldGetWorkingFeed() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);

        TimeChunkingHistoryStub history = new TimeChunkingHistoryStub();
        history.setCurrentSequenceNumber(4);
        history.setTimeRange(4, new TimeRange(startTime,startTime.plusHours(2)));

        AllEventRecords allEventRecordsStub = new AllEventRecordsStub();
        EventRecord eventRecord = new EventRecord(null, null, new URI(""), null, startTime.plusHours(1).toDate(), "");
        allEventRecordsStub.add(eventRecord);

        FeedGenerator generator = new TimeFeedGenerator(history,allEventRecordsStub);

        EventFeed recentFeed = generator.getRecentFeed();
        Assert.assertEquals(4, recentFeed.getId().intValue());
        Assert.assertSame(eventRecord, recentFeed.getEvents().get(0));
    }

    @Test(expected = AtomFeedRuntimeException.class)
    public void shouldThrowExceptionWhenRequestedForFeedInTheFuture(){
        TimeChunkingHistoryStub history = new TimeChunkingHistoryStub();
        history.setCurrentSequenceNumber(42);
        FeedGenerator generator = new TimeFeedGenerator(history,null);
        generator.getFeedForId(43, null);
    }
}
