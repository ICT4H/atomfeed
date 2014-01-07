package org.ict4h.atomfeed.server.service.feedgenerator;

import junit.framework.Assert;
import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TimeFeedGeneratorTest {

    @Test
    public void shouldGetWorkingFeed() throws Exception {
        final LocalDateTime startTime = LocalDateTime.now().minusHours(3);
        ChunkingEntries allChunkingEntries = new ChunkingEntries() {
            @Override
            public List<ChunkingHistoryEntry> all() {

                List<ChunkingHistoryEntry> entries = new ArrayList<>();
                entries.add(new ChunkingHistoryEntry(1, 7200000L, startTime.toDate().getTime()));
                return entries;
            }
        };

        AllEventRecords allEventRecordsStub = new AllEventRecordsStub();
        EventRecord eventRecord = new EventRecord(null, null, new URI(""), null, startTime.plusHours(2).toDate(), "");
        allEventRecordsStub.add(eventRecord);

        FeedGenerator generator = new TimeFeedGenerator(allEventRecordsStub, allChunkingEntries);

        EventFeed recentFeed = generator.getRecentFeed(null);
        Assert.assertEquals(2, recentFeed.getId().intValue());
        Assert.assertSame(eventRecord, recentFeed.getEvents().get(0));
    }

    @Test(expected = AtomFeedRuntimeException.class)
    public void shouldThrowExceptionWhenRequestedForFeedInTheFuture() {

        ChunkingEntries allChunkingEntries = new ChunkingEntries() {
            @Override
            public List<ChunkingHistoryEntry> all() {
                final LocalDateTime startTime = LocalDateTime.now().minusHours(3);
                List<ChunkingHistoryEntry> entries = new ArrayList<>();
                entries.add(new ChunkingHistoryEntry(1, 7200000L, startTime.toDate().getTime()));
                return entries;
            }
        };

        FeedGenerator generator = new TimeFeedGenerator(null, allChunkingEntries);
        generator.getFeedForId(43, null);
    }
}
