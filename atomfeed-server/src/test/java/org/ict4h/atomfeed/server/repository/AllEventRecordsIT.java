package org.ict4h.atomfeed.server.repository;

import org.apache.commons.lang3.StringUtils;
import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static ch.lambdaj.Lambda.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.startsWith;


public class AllEventRecordsIT extends IntegrationTest {

    private AllEventRecords allEventRecords;
    private JdbcConnectionProvider connectionProvider;
    private AFTransactionManager atomfeedTransactionManager;

    @Before
    public void before() throws SQLException {
        connectionProvider = getConnectionProvider();
        atomfeedTransactionManager = getAtomfeedTransactionManager(connectionProvider);
        allEventRecords = new AllEventRecordsJdbcImpl(connectionProvider);

        clearRecords();
    }

    @After
    public void after() throws SQLException {
        clearRecords();
    }

    private void clearRecords() {
        atomfeedTransactionManager.executeWithTransaction(new AFTransactionWorkWithoutResult() {
            @Override
            protected void doInTransaction() {
                try {
                    Statement statement = connectionProvider.getConnection().createStatement();
                    String event_records_table = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "event_records");
                    statement.execute(String.format("delete from %s", event_records_table));
                    String event_records_marker_table = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "event_records_offset_marker");
                    statement.execute(String.format("delete from %s", event_records_marker_table));
                    String chunking_history_table = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "chunking_history");
                    statement.execute(String.format("delete from %s", chunking_history_table));
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error occurred while trying to clear records.", e);
                }
            }

            @Override
            public PropagationDefinition getTxPropagationDefinition() {
                return PropagationDefinition.PROPAGATION_REQUIRES_NEW; //doesn't matter
            }
        });

    }

    @Test
    public void shouldAddEventRecordAndFetchByUUID() throws Exception {
        System.out.println("executing shouldAddEventRecordAndFetchByUUID");
        String uuid = UUID.randomUUID().toString();
        Date dateCreated = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("10-10-2015 12:35:00");
        EventRecord eventRecordAdded = new EventRecord(uuid, "title", "http://uri", null, dateCreated, "category");
        allEventRecords.add(eventRecordAdded);
        EventRecord eventRecordFetched = allEventRecords.get(uuid);
        assertEquals(eventRecordAdded.getUuid(), eventRecordFetched.getUuid());
        assertEquals(eventRecordAdded.getTitle(), eventRecordFetched.getTitle());
        assertEquals(eventRecordAdded.getCategory(), eventRecordFetched.getCategory());
        assertTrue((new Date()).after(eventRecordFetched.getTimeStamp()));
        assertEquals(dateCreated, eventRecordFetched.getDateCreated());
    }

    @Test
    public void shouldGetTotalCountOfEventRecordsWithCategory() throws URISyntaxException {
        System.out.println("executing shouldGetTotalCountOfEventRecords");
        String category = "category";
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", "http://uri", null, new Date(), category);
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", "http://uri", null, new Date(), "anotherCategory");

        allEventRecords.add(eventRecordAdded1);
        allEventRecords.add(eventRecordAdded2);

        int totalCount = allEventRecords.getTotalCountForCategory(category);
        Assert.assertEquals(1, totalCount);
    }

    @Test
    public void shouldGetTotalCountOfEventRecordsWithoutCategory() throws URISyntaxException {
        System.out.println("executing shouldGetTotalCountOfEventRecords");
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", "http://uri", null, new Date(), "someCategory");
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", "http://uri", null, new Date(), "someOtherCategory");

        allEventRecords.add(eventRecordAdded1);
        allEventRecords.add(eventRecordAdded2);

        int totalCount = allEventRecords.getTotalCountForCategory(null);
        Assert.assertEquals(2, totalCount);
    }

    @Test
    //relies on the rdbms serial id
    public void shouldGetEventsFromStartNumber() throws URISyntaxException {
        System.out.println("executing shouldGetEventsFromStartNumber");
        String category = "category";
        addEvents(6, "uuid", category);
        EventRecord e2 = allEventRecords.get("uuid2");
        EventRecord e5 = allEventRecords.get("uuid5");

        List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(category, 1, 4, 0);

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
        List<EventRecord> events = allEventRecords.getEventsFromTimeRange(new TimeRange(startTime, endTime), null);
        EventRecord firstEvent = events.get(0);
        EventRecord lastEvent = events.get(5);
        assertEquals("Event1",firstEvent.getTitle());
        assertEquals("Event6",lastEvent.getTitle());
    }

    @Test
    public void shouldFindEventsInTimeRangeThatBelongToACategory() throws URISyntaxException, InterruptedException {
        Timestamp startTime = new Timestamp(new Date().getTime());
        String firstCategory = "oneCategory";
        addEvents(2,"uuid1", firstCategory);
        addEvents(3,"uuid2","anotherCategory");
        addEvents(5,"uuid3", firstCategory);
        // Adding an extra millisecond below to account for the discrepancy that system time is stored
        // in the DB in nanoseconds while new Date().getTime() only returns time till millisecond accuracy.
        Timestamp endTime = new Timestamp(new Date().getTime() + 1);
        List<EventRecord> events = allEventRecords.getEventsFromTimeRange(new TimeRange(startTime, endTime), firstCategory);
        assertEquals(7,events.size());
        Assert.assertTrue(filter(having(on(EventRecord.class).getUuid(), startsWith("uuid2")), events).isEmpty());
    }

    @Test
    public void shouldFetchEventsFilteredByCategory() throws URISyntaxException {
        String firstCategory = "oneCategory";
        addEvents(2,"uuid1", firstCategory);
        addEvents(3,"uuid2","another");
        addEvents(5,"uuid3", firstCategory);
        List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(firstCategory, 2, 3, 0);
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
        List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(null, 0, 12, 0);
        assertEquals(12,events.size());
    }

    @Test
    public void shouldFetchEventsForCategoryAndGivenBounds() throws Exception {
        generateData(100, null);
        int totalCountForCategory = allEventRecords.getTotalCountForCategory("Cat-1", 0, null);
        assertEquals(50, totalCountForCategory);
        totalCountForCategory = allEventRecords.getTotalCountForCategory("Cat-0", 0, null);
        assertEquals(50, totalCountForCategory);
        totalCountForCategory = allEventRecords.getTotalCountForCategory(null, 0, null);
        assertEquals(100, totalCountForCategory);
    }

    @Test
    public void shouldGetAllDistinctCategories() throws Exception {
        generateData(10, "Cat1");
        generateData(10, "Cat2");
        generateData(10, "Cat3");
        generateData(1, "Cat4");
        generateData(1, "Cat1");
        List<String> categories = allEventRecords.findCategories();
        assertEquals(4, categories.size());
        assertTrue(categories.indexOf("Cat1") >=  0);
        assertTrue(categories.indexOf("Cat3") >=  0);
    }

    private void addEvents(int numberOfEvents, String uuidStartsWith, String category) throws URISyntaxException {
        for (int i = 1; i <= numberOfEvents; i++) {
            String title = "Event" + i;
            allEventRecords.add(new EventRecord(uuidStartsWith + i, title, "http://uri/" + title, null, new Date(), category));
        }
    }

    private void generateData(int total, String eventCategory) throws URISyntaxException {
        for (int i = 0; i < total; i++) {
            String uuid = UUID.randomUUID().toString();
            String category = StringUtils.isBlank(eventCategory) ? (((i % 2) == 0) ? "Cat-0" : "Cat-1") : eventCategory;
            EventRecord eventRecordAdded = new EventRecord(uuid, "title-" + i, "http://uri/" + i, "content-" + uuid, new Date(), category);
            allEventRecords.add(eventRecordAdded);
        }
    }




}
