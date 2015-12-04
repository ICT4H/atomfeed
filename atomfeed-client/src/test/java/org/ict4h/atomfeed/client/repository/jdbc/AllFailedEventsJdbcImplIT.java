package org.ict4h.atomfeed.client.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.domain.FailedEventRetryLog;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class AllFailedEventsJdbcImplIT extends IntegrationTest {

    private AllFailedEventsJdbcImpl allFailedEvents;
    private JdbcConnectionProvider connectionProvider;
    private AFTransactionManager atomfeedTransactionManager;

    private void clearRecords() throws SQLException {
        atomfeedTransactionManager.executeWithTransaction(
                new AFTransactionWorkWithoutResult() {
                    @Override
                    protected void doInTransaction() {
                        try {
                            clearTable("failed_events");
                            clearTable("failed_event_retry_log");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public PropagationDefinition getTxPropagationDefinition() {
                        return PropagationDefinition.PROPAGATION_REQUIRED;
                    }
                }
        );
    }

    private void clearTable(String tableName) throws Exception {
        Statement statement = connectionProvider.getConnection().createStatement();
        String table = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), tableName);
        statement.execute("delete from " + table);
        statement.close();
    }

    @Before
    public void setUp() throws SQLException {
        connectionProvider = getConnectionProvider();
        atomfeedTransactionManager = getAtomfeedTransactionManager(connectionProvider);
        allFailedEvents = new AllFailedEventsJdbcImpl(connectionProvider);
    }

    @After
    public void tearDown() throws SQLException {
        clearRecords();
    }

    @Test
    public void shouldCRUDFailedEvent() throws Exception {
        String feedUri = "http://feedUri";
        String errorMessage = "errorMessage";
        long failedAt = new Date().getTime();
        Event event = new Event("eventId", "eventContent", "title");
        FailedEvent failedEvent = new FailedEvent(feedUri, event, errorMessage, failedAt, 5);

        allFailedEvents.addOrUpdate(failedEvent);
        FailedEvent failedEventDb = allFailedEvents.get(feedUri, event.getId());
        assertFailedEvent(failedEvent, failedEventDb);

        String newErrorMessage = "newErrorMessage";
        long newFailedAt = new Date().getTime();
        FailedEvent modifiedFailedEvent = new FailedEvent(failedEvent.getFeedUri(), failedEvent.getEvent(), newErrorMessage, newFailedAt, 0);

        allFailedEvents.addOrUpdate(modifiedFailedEvent);
        failedEventDb = allFailedEvents.get(feedUri, event.getId());

        // the error in failed events table is always the original error. Subsequent retry errors are in the retry table
        assertEquals(errorMessage, failedEventDb.getErrorMessage());

        allFailedEvents.remove(modifiedFailedEvent);
        failedEventDb = allFailedEvents.get(feedUri, event.getId());

        assertNull(failedEventDb);
    }

    @Test
    public void shouldDeleteFailedEventsAndRetryLogsTogether() throws  Exception {
        String feedUri = "http://feedUri";
        String errorMessage = "errorMessage";
        long failedAt = new Date().getTime();
        Event event = new Event("eventId", "eventContent", "title");
        FailedEvent failedEvent = new FailedEvent(feedUri, event, errorMessage, failedAt, 5);
        allFailedEvents.addOrUpdate(failedEvent);
        FailedEventRetryLog failedEventRetryLog = new FailedEventRetryLog(feedUri, failedAt, errorMessage, event.getId(), event.getContent());
        allFailedEvents.insert(failedEventRetryLog);

        allFailedEvents.remove(failedEvent);

        FailedEvent failedEventDb = allFailedEvents.get(feedUri, event.getId());
        assertNull(failedEventDb);
        ResultSet failedEventRetryLogResultSet = getFailedEventRetryLog(feedUri, event.getId());
        assertFalse("Failed event retry logs are not deleted", failedEventRetryLogResultSet.next());
    }

    @Test
    public void shouldTrimErrorMessagesLongerThan4k() {
        String feedUri = "http://feedUri";
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 4500; i++) sb.append("*");
        String errorMessage = sb.toString();
        long failedAt = new Date().getTime();
        Event event = new Event("eventId", "eventContent", "title");
        FailedEvent failedEvent = new FailedEvent(feedUri, event, errorMessage, failedAt, 5);

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
        assertEquals(expectedFailedEvent.getEvent().getTitle(), actualFailedEvent.getEvent().getTitle());
        assertEquals(expectedFailedEvent.getRetries(), actualFailedEvent.getRetries());
    }

    private List<FailedEvent> setupWith5Events() {
        String feedUri = "http://feedUri";
        String errorMessage = "errorMessage";
        long failedAt = new Date().getTime();

        List<FailedEvent> failedEvents = new ArrayList<>();
        for (int i = 1; i <= 5; i ++) {
            FailedEvent failedEvent = new FailedEvent(feedUri, new Event("eventId" + i, "eventContent1" + i, "title"), errorMessage, 0);
            failedEvents.add(failedEvent);
            allFailedEvents.addOrUpdate(failedEvent);
        }

        return failedEvents;
    }

    @Test
    public void shouldGetOldestNFailedEvents() throws Exception {
        List<FailedEvent> failedEvents = setupWith5Events();

        List<FailedEvent> oldestNFailedEvents = allFailedEvents.getOldestNFailedEvents(failedEvents.get(0).getFeedUri(), 3, 5);

        assertEquals(3, oldestNFailedEvents.size());
        assertEquals(failedEvents.get(0).getEventId(), oldestNFailedEvents.get(0).getEventId());
        assertEquals(failedEvents.get(1).getEventId(), oldestNFailedEvents.get(1).getEventId());
        assertEquals(failedEvents.get(2).getEventId(), oldestNFailedEvents.get(2).getEventId());

        oldestNFailedEvents = allFailedEvents.getOldestNFailedEvents(failedEvents.get(0).getFeedUri(), 7, 5);
        assertEquals(5, oldestNFailedEvents.size());
    }

    @Test
    public void shouldNotGetFailedEventsIfItHasReachedMaxRetries() {
        List<FailedEvent> failedEvents = setupWith5Events();
        failedEvents.get(0).setRetries(5);
        allFailedEvents.addOrUpdate(failedEvents.get(0));

        List<FailedEvent> oldestNFailedEvents = allFailedEvents.getOldestNFailedEvents(failedEvents.get(0).getFeedUri(), 3, 5);
        assertEquals(3, oldestNFailedEvents.size());
        assertEquals(failedEvents.get(1).getEventId(), oldestNFailedEvents.get(0).getEventId());
        assertEquals(failedEvents.get(2).getEventId(), oldestNFailedEvents.get(1).getEventId());
        assertEquals(failedEvents.get(3).getEventId(), oldestNFailedEvents.get(2).getEventId());
    }

    @Test
    public void testGetNumberOfFailedEvents() throws Exception {
        List<FailedEvent> failedEvents = setupWith5Events();

        assertEquals(5, allFailedEvents.getNumberOfFailedEvents(failedEvents.get(0).getFeedUri()));
    }

    @Test
    public void ShouldInsertIntoFailedEventsRetryLog() throws Exception {
        String feedUri = "http://feedUri";
        long failedAt = new Date().getTime();
        String errorMessage = "errorMessage";
        String eventId = "event1";
        String eventContent = "content";
        FailedEventRetryLog failedEventRetryLog = new FailedEventRetryLog(feedUri, failedAt, errorMessage, eventId, eventContent);
        allFailedEvents.insert(failedEventRetryLog);

        ResultSet resultSet = getFailedEventRetryLog(feedUri, eventId);
        resultSet.next();

        assertNotNull(resultSet.getInt(1));
        assertEquals(feedUri, resultSet.getString(2));
        assertEquals(new Timestamp(failedAt), resultSet.getTimestamp(3));
        assertEquals(errorMessage, resultSet.getString(4));
        assertEquals(eventId, resultSet.getString(5));
        assertEquals(eventContent, resultSet.getString(6));
        assertEquals(errorMessage.hashCode(), resultSet.getInt(7));
    }

    private ResultSet getFailedEventRetryLog(String feedUri, String eventId) throws Exception {
        String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), AllFailedEventsJdbcImpl.FAILED_EVENT_RETRY_LOG_TABLE);
        PreparedStatement fetchStatement = connectionProvider.getConnection().prepareStatement(String.format("select id, feed_uri, failed_at, error_message, event_id, event_content, error_hash_code from %s where feed_uri = ? and event_id = ?", tableName));
        fetchStatement.setString(1, feedUri);
        fetchStatement.setString(2, eventId);

        return fetchStatement.executeQuery();
    }
}
