package org.ict4htw.atomfeed.server.feed;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class FeedGeneratorIT extends SpringIntegrationIT {

    @Autowired
    private AllEventRecords allEventRecords;

    @Before
    @After
    public void purgeEventRecords() {
        template.deleteAll(template.loadAll(EventRecord.class));
    }

    private final int ENTRIES_PER_FEED = 5;

    @Test
    public void shouldCheckRecentFeedEntries() {
        int unarchivedCount = allEventRecords.getUnarchivedEventsCount();
        int totalCount = allEventRecords.getTotalCount();
        if (totalCount > 0) {
            assertTrue("Unarchived events must be greater than zero", unarchivedCount > 0);
        }
    }

    @Test
    public void shouldGetRecentFeeds() throws URISyntaxException {
        allEventRecords.add(new EventRecord(UUID.randomUUID().toString(), "entry 1", new URI("http://uri/entry1"), null));
        allEventRecords.add(new EventRecord(UUID.randomUUID().toString(), "entry 2", new URI("http://uri/entry2"), null));
        String entry3UID = UUID.randomUUID().toString();
        allEventRecords.add(new EventRecord(entry3UID, "entry 3", new URI("http://uri/entry3"), null));
        List<EventRecord> recentFeed = allEventRecords.getUnarchivedEvents(2);
        for (EventRecord eventRecord : recentFeed) {
            assertFalse("Should not have fetched the last entered record", eventRecord.getUuid().equals(entry3UID));
        }
    }


    public static void main(String[] args) {
        System.out.println("* " + (5 / 5));

//			AllEventRecords eventRecords = new AllEventRecordsStub();
//			FeedGenerator generator = new FeedGenerator(eventRecords);
//			generator.archiveFeeds();

    }

}
