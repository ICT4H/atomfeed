package org.ict4h.atomfeed.server.service.feedgenerator;

import junit.framework.Assert;
import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.EventRecordsOffsetMarker;
import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.EventRecordsOffsetMarkers;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;


public class NumberFeedGeneratorTest {

    AllEventRecords eventsRecord = new AllEventRecordsStub();
    private EventRecordsOffsetMarkers eventRecordsOffsetMarkers = new EventRecordsOffsetMarkers() {
        @Override
        public void setOffSetMarkerForCategory(String category, Integer offsetId, Integer countTillOffSetId) {
        }

        @Override
        public List<EventRecordsOffsetMarker> getAll() {
            return new ArrayList<>();
        }
    };
    private NumberFeedGenerator feedGenerator;

    private ChunkingEntries allChunkingEntries = new ChunkingEntries() {
        @Override
        public List<ChunkingHistoryEntry> all() {
            List<ChunkingHistoryEntry> entries = new ArrayList<>();
            entries.add(new ChunkingHistoryEntry(1, 5L, 1L));
            return entries;
        }
    };

    @Before
    public void setUp() {
        feedGenerator = new NumberFeedGenerator(eventsRecord, eventRecordsOffsetMarkers, allChunkingEntries);
    }

    @Test(expected = AtomFeedRuntimeException.class)
    public void shouldErrorOutForMissingId() {
        feedGenerator.getFeedForId(null, null);
    }

    @Test(expected = AtomFeedRuntimeException.class)
    public void shouldErrorOutForInvalidId() {
        feedGenerator.getFeedForId(0, null);
    }

    @Test(expected = Exception.class)
    public void shouldErrorOutForFutureFeed() {
        feedGenerator.getFeedForId(999, null);
    }

    @Test
    public void shouldRetrieveGivenFeed() throws Exception {
        addEvents(11);
        EventFeed feed = feedGenerator.getFeedForId(1, "category");
        Assert.assertEquals(1, feed.getId().intValue());
        Assert.assertEquals(5, feed.getEvents().size());
    }

    @Test
    public void shouldRetrieveRecentFeed() throws Exception {
        addEvents(15);
        EventFeed feed = feedGenerator.getRecentFeed(null);
        Assert.assertEquals(3, feed.getId().intValue());
        Assert.assertEquals(5, feed.getEvents().size());
    }

    @Test
    public void shouldRetrieveEmptyFeedForWhenRecentFeedIsQueriedForWithNoEventsPresent() {
        AllEventRecords eventRecords = mock(AllEventRecords.class);
        feedGenerator = new NumberFeedGenerator(eventRecords, eventRecordsOffsetMarkers, allChunkingEntries);
        stub(eventRecords.getTotalCountForCategory(anyString())).toReturn(0);
        EventFeed feed = feedGenerator.getRecentFeed("");

        Assert.assertEquals(new Integer(0), feed.getId());
        Assert.assertTrue(feed.getEvents().isEmpty());
        verify(eventRecords, never()).getEventsFromRangeForCategory(anyString(), anyInt(), anyInt(), anyInt());
    }

    private void addEvents(int eventNumber) throws URISyntaxException {
        for (int i = 1; i <= eventNumber; i++) {
            String title = "Event" + i;
            eventsRecord.add(new EventRecord(UUID.randomUUID().toString(), title, new URI("http://uri/" + title), null, new Date(), "category"));
        }
    }
}
