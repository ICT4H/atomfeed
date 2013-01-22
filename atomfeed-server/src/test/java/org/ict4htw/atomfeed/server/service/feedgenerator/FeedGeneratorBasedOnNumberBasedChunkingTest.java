package org.ict4htw.atomfeed.server.service.feedgenerator;

import junit.framework.Assert;
import org.ict4htw.atomfeed.server.domain.EventFeed;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;


public class FeedGeneratorBasedOnNumberBasedChunkingTest {
    AllEventRecords eventsRecord = new AllEventRecordsStub();
    private FeedGeneratorBasedOnNumberBasedChunking feedGenerator;

    @Before
    public void setUp() {
        NumberBasedChunkingHistory config = new NumberBasedChunkingHistory();
        config.add(1, 5, 1);
        feedGenerator = new FeedGeneratorBasedOnNumberBasedChunking(eventsRecord, config);
    }

    @Test(expected = Exception.class)
    public void shouldErrorOutForMissingId() {
        feedGenerator.getFeedForId(null);
    }

    @Test(expected = Exception.class)
    public void shouldErrorOutForInvalidId() {
        feedGenerator.getFeedForId(0);
    }

    @Test(expected = Exception.class)
    public void shouldErrorOutForFutureFeed() {
        feedGenerator.getFeedForId(999);
    }

    @Test
    public void shouldRetrieveGivenFeed() throws Exception {
        addEvents(11);
        EventFeed feed = feedGenerator.getFeedForId(1);
        Assert.assertEquals(1, feed.getId().intValue());
        Assert.assertEquals(5, feed.getEvents().size());
    }

    @Test
    public void shouldRetrieveRecentFeed() throws Exception {
        addEvents(15);
        EventFeed feed = feedGenerator.getRecentFeed();
        Assert.assertEquals(3, feed.getId().intValue());
        Assert.assertEquals(5, feed.getEvents().size());
    }

    private void addEvents(int eventNumber) throws URISyntaxException {
        for (int i = 1; i <= eventNumber; i++) {
            String title = "Event" + i;
            eventsRecord.add(new EventRecord(UUID.randomUUID().toString(), title, new URI("http://uri/" + title), null,new Date()));
        }
    }


}
