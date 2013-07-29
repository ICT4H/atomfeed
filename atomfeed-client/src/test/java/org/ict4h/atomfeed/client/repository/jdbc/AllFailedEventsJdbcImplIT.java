package org.ict4h.atomfeed.client.repository.jdbc;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class AllFailedEventsJdbcImplIT extends IntegrationTest{

    private AllFailedEventsJdbcImpl allFailedEvents;
    private Connection connection;

    private void clearRecords() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("delete from atomfeed.failed_events");
        statement.close();
    }

    @Before
    public void setUp() throws SQLException {
        connection = getConnection();
        allFailedEvents = new AllFailedEventsJdbcImpl(getProvider(connection));
    }

    @After
    public void tearDown() throws SQLException {
        clearRecords();
        connection.close();
    }

    @Test
    public void shouldCRUDFailedEvent() throws Exception {
        String feedUri = "http://feedUri";
        String errorMessage = "errorMessage";
        long failedAt = new Date().getTime();
        Event event = new Event("eventId", "eventContent");
        FailedEvent failedEvent = new FailedEvent(feedUri, event, errorMessage, failedAt);

        allFailedEvents.addOrUpdate(failedEvent);
        FailedEvent failedEventDb = allFailedEvents.get(feedUri, event.getId());
        assertFailedEvent(failedEvent, failedEventDb);

        String newErrorMessage = "newErrorMessage";
        long newFailedAt = new Date().getTime();
        FailedEvent modifiedFailedEvent = new FailedEvent(failedEvent.getFeedUri(), failedEvent.getEvent(), newErrorMessage, newFailedAt);

        allFailedEvents.addOrUpdate(modifiedFailedEvent);
        failedEventDb = allFailedEvents.get(feedUri, event.getId());

        assertFailedEvent(modifiedFailedEvent, failedEventDb);

        allFailedEvents.remove(modifiedFailedEvent);
        failedEventDb = allFailedEvents.get(feedUri, event.getId());

        assertNull(failedEventDb);
    }

    @Test
    public void shouldTrimErrorMessagesLongerThan4k() {
        String feedUri = "http://feedUri";
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 4500; i++) sb.append("*");
        String errorMessage = sb.toString();
        long failedAt = new Date().getTime();
        Event event = new Event("eventId", "eventContent");
        FailedEvent failedEvent = new FailedEvent(feedUri, event, errorMessage, failedAt);

        allFailedEvents.addOrUpdate(failedEvent);
        FailedEvent failedEventDb = allFailedEvents.get(feedUri, event.getId());

        assertNotSame(errorMessage, failedEventDb.getErrorMessage());
        assertEquals(4000, failedEventDb.getErrorMessage().length());

        failedEvent.setErrorMessage(errorMessage);
        allFailedEvents.addOrUpdate(failedEvent);
        failedEventDb = allFailedEvents.get(feedUri, event.getId());

        assertNotSame(errorMessage, failedEventDb.getErrorMessage());
        assertEquals(4000, failedEventDb.getErrorMessage().length());
    }

    private void assertFailedEvent(FailedEvent expectedFailedEvent, FailedEvent actualFailedEvent) {
        assertEquals(expectedFailedEvent.getFeedUri(), actualFailedEvent.getFeedUri());
        assertEquals(expectedFailedEvent.getErrorMessage(), actualFailedEvent.getErrorMessage());
        assertEquals(expectedFailedEvent.getFailedAt(), actualFailedEvent.getFailedAt());
        assertEquals(expectedFailedEvent.getEventId(), actualFailedEvent.getEventId());
        assertEquals(expectedFailedEvent.getEvent().getContent(), actualFailedEvent.getEvent().getContent());
    }

    private List<FailedEvent> setupWith5Events() {
        String feedUri = "http://feedUri";
        String errorMessage = "errorMessage";
        long failedAt = new Date().getTime();

        List<FailedEvent> failedEvents = new ArrayList<>();
        for (int i = 1; i <= 5; i ++) {
            FailedEvent failedEvent = new FailedEvent(feedUri, new Event("eventId" + i, "eventContent1" + i), errorMessage);
            failedEvents.add(failedEvent);
            allFailedEvents.addOrUpdate(failedEvent);
        }

        return failedEvents;
    }

    @Test
    public void shouldGetOldestNFailedEvents() throws Exception {
        List<FailedEvent> failedEvents = setupWith5Events();

        List<FailedEvent> oldestNFailedEvents = allFailedEvents.getOldestNFailedEvents(failedEvents.get(0).getFeedUri(), 3);

        assertEquals(3, oldestNFailedEvents.size());
        assertEquals(failedEvents.get(0).getEventId(), oldestNFailedEvents.get(0).getEventId());
        assertEquals(failedEvents.get(1).getEventId(), oldestNFailedEvents.get(1).getEventId());
        assertEquals(failedEvents.get(2).getEventId(), oldestNFailedEvents.get(2).getEventId());

        oldestNFailedEvents = allFailedEvents.getOldestNFailedEvents(failedEvents.get(0).getFeedUri(), 7);
        assertEquals(5, oldestNFailedEvents.size());
    }

    @Test
    public void testGetNumberOfFailedEvents() throws Exception {
        List<FailedEvent> failedEvents = setupWith5Events();

        assertEquals(5, allFailedEvents.getNumberOfFailedEvents(failedEvents.get(0).getFeedUri()));
    }
}
