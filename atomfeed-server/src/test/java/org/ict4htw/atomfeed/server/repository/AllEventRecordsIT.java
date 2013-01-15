package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.server.domain.EventArchive;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.feed.FeedArchiver;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.*;

public class AllEventRecordsIT extends SpringIntegrationIT {

    @Autowired
    private AllEventRecords allEventRecords;

    @Before
    @After
    public void purgeEventRecords() {
        template.deleteAll(template.loadAll(EventRecord.class));
        template.deleteAll(template.loadAll(EventArchive.class));

    }

    @Test
    public void shouldAddEventRecordAndFetchByUUID() throws URISyntaxException {
        String uuid = UUID.randomUUID().toString();

        EventRecord eventRecordAdded = new EventRecord(uuid, "title", new URI("http://uri"), null);

        allEventRecords.add(eventRecordAdded);

        EventRecord eventRecordFetched = allEventRecords.get(uuid);

        assertEquals(eventRecordAdded.getUuid(), eventRecordFetched.getUuid());
        assertEquals(eventRecordAdded.getTitle(), eventRecordFetched.getTitle());
        assertTrue((new Date()).after(eventRecordFetched.getTimeStamp()));
    }

    @Test
    public void shouldGetTotalCountOfEventRecords() throws URISyntaxException {
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", new URI("http://uri"), null);
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", new URI("http://uri"), null);

        allEventRecords.add(eventRecordAdded1);
        allEventRecords.add(eventRecordAdded2);

        int totalCount = allEventRecords.getTotalCount();

        Assert.assertEquals(2, totalCount);
    }

    @Test
    @Ignore
    public void shouldGetEventsFromStartNumber() throws URISyntaxException {
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", new URI("http://uri"), null);
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", new URI("http://uri"), null);
        EventRecord eventRecordAdded3 = new EventRecord("uuid3", "title", new URI("http://uri"), null);
        EventRecord eventRecordAdded4 = new EventRecord("uuid4", "title", new URI("http://uri"), null);
        EventRecord eventRecordAdded5 = new EventRecord("uuid5", "title", new URI("http://uri"), null);

        allEventRecords.add(eventRecordAdded1);
        allEventRecords.add(eventRecordAdded2);
        allEventRecords.add(eventRecordAdded3);
        allEventRecords.add(eventRecordAdded4);
        allEventRecords.add(eventRecordAdded5);

        List<EventRecord> eventRecordList = allEventRecords.getEventsFromNumber(3, 2);

        assertEquals(2, eventRecordList.size());
        assertEquals(eventRecordAdded4.getUuid(), eventRecordList.get(0).getUuid());
        assertEquals(eventRecordAdded5.getUuid(), eventRecordList.get(1).getUuid());
    }

    @Test
	public void shouldCheckRecentFeedEntries() {
		int unarchivedCount = allEventRecords.getUnarchivedEventsCount();
		int totalCount = allEventRecords.getTotalCount();
		if (totalCount > 0) {
			assertTrue("Unarchived events must be greater than zero", unarchivedCount > 0);    
		}
	}
    
    @Test
	public void shouldGetEventsInOrderOfCreation() throws URISyntaxException {
        allEventRecords.add(new EventRecord(UUID.randomUUID().toString(), "entry 1", new URI("http://uri/entry1"), null));
        allEventRecords.add(new EventRecord(UUID.randomUUID().toString(), "entry 2", new URI("http://uri/entry2"), null));
        String entry3UID = UUID.randomUUID().toString();
		allEventRecords.add(new EventRecord(entry3UID, "entry 3", new URI("http://uri/entry3"), null));
		List<EventRecord> recentFeed = allEventRecords.getUnarchivedEvents(2);
		for (EventRecord eventRecord : recentFeed) {
			assertFalse("Should not have fetched the last entered record", eventRecord.getUuid().equals(entry3UID)); 
		}
	}


    @Test
    public void testShouldFindArchiveByIdAndCheckParentIds() throws Exception {
        addEvents(14);
        FeedArchiver feedArchiver = new FeedArchiver(allEventRecords);
        feedArchiver.archiveFeeds();
        EventArchive latestArchive = allEventRecords.getLatestArchive();
        assertNotNull(latestArchive);
        assertNotNull(latestArchive.getParentId());
        EventArchive archiveById = allEventRecords.findArchiveById(latestArchive.getParentId());
        assertNotNull(archiveById);
    }

    private void addEvents(int eventNumber) throws URISyntaxException {
        for (int i= 1; i <= eventNumber; i++) {
            String title = "Event" + i;
            allEventRecords.add(new EventRecord(UUID.randomUUID().toString(), title, new URI("http://uri/"+title), null));
        }
    }
}
