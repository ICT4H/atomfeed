package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class AllEventRecordsIT extends IntegrationTest {

    private AllEventRecords allEventRecords;
    private Connection connection;

    @Before
    public void before() throws SQLException {
        connection = getConnection();
        allEventRecords = new AllEventRecordsJdbcImpl(getProvider(connection));
        clearRecords();
    }

    @After
    public void after() throws SQLException {
        clearRecords();
        connection.close();
    }

    private void clearRecords() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("delete from atomfeed.event_records");
        statement.close();
    }

    @Test
    public void shouldAddEventRecordAndFetchByUUID() throws URISyntaxException, SQLException {
        System.out.println("executing shouldAddEventRecordAndFetchByUUID");
        String uuid = UUID.randomUUID().toString();
        EventRecord eventRecordAdded = new EventRecord(uuid, "title", new URI("http://uri"), null, new Date(), "category");
        allEventRecords.add(eventRecordAdded);
        EventRecord eventRecordFetched = allEventRecords.get(uuid);
        assertEquals(eventRecordAdded.getUuid(), eventRecordFetched.getUuid());
        assertEquals(eventRecordAdded.getTitle(), eventRecordFetched.getTitle());
        assertEquals(eventRecordAdded.getCategory(), eventRecordFetched.getCategory());
        assertTrue((new Date()).after(eventRecordFetched.getTimeStamp()));
    }

    @Test
    public void shouldGetTotalCountOfEventRecords() throws URISyntaxException {
        System.out.println("executing shouldGetTotalCountOfEventRecords");
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", new URI("http://uri"), null, new Date(), "");
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", new URI("http://uri"), null, new Date(), "");

        allEventRecords.add(eventRecordAdded1);
        allEventRecords.add(eventRecordAdded2);

        int totalCount = allEventRecords.getTotalCount();
        Assert.assertEquals(2, totalCount);
    }

    @Test
    //relies on the rdbms serial id
    public void shouldGetEventsFromStartNumber() throws URISyntaxException {
        System.out.println("executing shouldGetEventsFromStartNumber");
        addEvents(6, "uuid", "");
        EventRecord e2 = allEventRecords.get("uuid2");
        EventRecord e5 = allEventRecords.get("uuid5");

        List<EventRecord> events = allEventRecords.getEventsFromRange(e2.getId(), e5.getId());

        assertEquals(4, events.size());
        assertEquals(e2.getUuid(), events.get(0).getUuid());
        assertEquals(e5.getUuid(), events.get(events.size() - 1).getUuid());
    }

    @Test
    public void shouldFindEventsInTimeRange() throws URISyntaxException, InterruptedException {
        Timestamp startTime = new Timestamp(new Date().getTime());
        addEvents(6, "uuid", "");
        // Adding an extra millisecond below to account for the discrepancy that system time is stored
        // in the DB in nanoseconds while new Date().getTime() only returns time till millisecond accuracy.
        Timestamp endTime = new Timestamp(new Date().getTime() + 1);
        List<EventRecord> events = allEventRecords.getEventsFromTimeRange(new TimeRange(startTime, endTime));
        EventRecord firstEvent = events.get(0);
        EventRecord lastEvent = events.get(5);
        assertEquals("Event1",firstEvent.getTitle());
        assertEquals("Event6",lastEvent.getTitle());
    }

    @Test
    public void shouldFetchEventsFilteredByCategory() throws URISyntaxException {
        String firstCategory = "oneCategory";
        addEvents(2,"uuid1", firstCategory);
        addEvents(3,"uuid2","another");
        addEvents(5,"uuid3", firstCategory);
        List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(firstCategory, 2, 3);
        assertEquals(3,events.size());
        assertEquals("uuid31",events.get(0).getUuid());
        assertEquals("uuid32",events.get(1).getUuid());
        assertEquals("uuid33",events.get(2).getUuid());
    }

    @Test
    public void shouldFetchEventsAcrossCategoriesWhenCategoryIsNull() throws URISyntaxException {
        String firstCategory = "oneCategory";
        addEvents(5,"uuid1", firstCategory);
        String anotherCategory = "another";
        addEvents(7,"uuid2", anotherCategory);
        List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(null, 0, 12);
        assertEquals(12,events.size());
    }

    private void addEvents(int numberOfEvents, String uuidStartsWith, String category) throws URISyntaxException {
        for (int i = 1; i <= numberOfEvents; i++) {
            String title = "Event" + i;
            allEventRecords.add(new EventRecord(uuidStartsWith + i, title, new URI("http://uri/" + title), null, new Date(), category));
        }
    }
}
