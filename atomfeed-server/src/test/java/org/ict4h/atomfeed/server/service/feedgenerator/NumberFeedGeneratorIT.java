package org.ict4h.atomfeed.server.service.feedgenerator;

import org.apache.commons.lang3.StringUtils;
import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.AllEventRecordsOffsetMarkers;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsOffsetMarkersJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.service.NumberOffsetMarkerServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class NumberFeedGeneratorIT extends IntegrationTest {

    private JdbcConnectionProvider connectionProvider;
    private AllEventRecordsJdbcImpl allEventRecords;
    private NumberFeedGenerator feedGenerator;
    private NumberOffsetMarkerServiceImpl markerService;
    private AllEventRecordsOffsetMarkers allEventRecordsOffsetMarkers;
    private ChunkingEntries chunkingEntries;
    private AFTransactionManager atomfeedTransactionManager;

    @Before
    public void setUp() throws Exception {
        connectionProvider = getConnectionProvider();
        atomfeedTransactionManager = getAtomfeedTransactionManager(connectionProvider);

        clearRecords();
        addChunkingHistory(5, 1);

        allEventRecords = new AllEventRecordsJdbcImpl(connectionProvider);
        allEventRecordsOffsetMarkers = new AllEventRecordsOffsetMarkersJdbcImpl(connectionProvider);
        chunkingEntries = new ChunkingEntriesJdbcImpl(connectionProvider);

        feedGenerator = new NumberFeedGenerator(allEventRecords, allEventRecordsOffsetMarkers, chunkingEntries);
        markerService = new NumberOffsetMarkerServiceImpl(allEventRecords, chunkingEntries, allEventRecordsOffsetMarkers);
    }

    @After
    public void after() throws Exception {
        clearRecords();
    }

    @Test
    public void shouldGetRecentFeedForCategory() throws Exception {
        generateData(5, "Cat-0");
        generateData(5, "Cat-1");
        generateData(1, "Cat-0");
        generateData(3, "Cat-1");
        assertEquals(1, feedGenerator.getRecentFeed("Cat-0").getEvents().size());
        assertEquals(3, feedGenerator.getRecentFeed("Cat-1").getEvents().size());
    }

    @Test
    public void shouldGetFeed() throws Exception {
        generateData(3, "Cat-0");
        generateData(2, "Cat-1");
        generateData(13, "Cat-0");
        generateData(12, "Cat-1");
        assertEquals(5, feedGenerator.getFeedForId(1, "Cat-1").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(2, "Cat-0").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(2, "Cat-1").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(3, "Cat-0").getEvents().size());
        assertEquals(4, feedGenerator.getFeedForId(3, "Cat-1").getEvents().size());
        assertEquals(1, feedGenerator.getFeedForId(4, "Cat-0").getEvents().size());
    }

    @Test
    public void shouldGetFeedForCategoryWhenNoOffsetMarkerIsSet() throws Exception {
        generateData(66, null);
        assertEquals(5, feedGenerator.getFeedForId(1, "Cat-0").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(2, "Cat-0").getEvents().size());
        assertEquals(3, feedGenerator.getFeedForId(7, "Cat-0").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(1, "Cat-1").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(2, "Cat-1").getEvents().size());
        assertEquals(3, feedGenerator.getFeedForId(7, "Cat-1").getEvents().size());
    }

    @Test
    public void shouldGetFeedWhenOffsetMarkerIsSet() throws Exception {
        generateData(66, null);
        markerService.markEvents(new String[]{"Cat-0", "Cat-1", ""}, 20);
        assertEquals(5, feedGenerator.getFeedForId(1, "Cat-0").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(2, "Cat-0").getEvents().size());
        assertEquals(3, feedGenerator.getFeedForId(7, "Cat-0").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(1, "Cat-1").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(2, "Cat-1").getEvents().size());
        assertEquals(3, feedGenerator.getFeedForId(7, "Cat-1").getEvents().size());
    }

    @Test
    public void shouldGetFeedWithMultipleChunkingHistoriesWithoutMarkers() throws Exception {
        generateData(48, "Cat-1");
        assertEquals(5, feedGenerator.getFeedForId(1, "Cat-1").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(9, "Cat-1").getEvents().size());
        assertEquals(3, feedGenerator.getRecentFeed("Cat-1").getEvents().size());
        addChunkingHistory(9, 49);
        FeedGenerator newFeedGenerator = new NumberFeedGenerator(allEventRecords, allEventRecordsOffsetMarkers, chunkingEntries);
        assertEquals(3, newFeedGenerator.getRecentFeed("Cat-1").getEvents().size());
        assertEquals(3, newFeedGenerator.getFeedForId(10, "Cat-1").getEvents().size());
        generateData(49, "Cat-1");
        assertEquals(3, newFeedGenerator.getFeedForId(10, "Cat-1").getEvents().size());
        assertEquals(9, newFeedGenerator.getFeedForId(11, "Cat-1").getEvents().size());
        assertEquals(4, newFeedGenerator.getFeedForId(16, "Cat-1").getEvents().size());
    }

    @Test
    public void shouldGetFeedWithMultipleChunkingHistoriesWithMarkers() throws Exception {
        generateData(48, "Cat-1");
        markerService.markEvents(new String[]{"Cat-1", ""}, 20);
        assertEquals(5, feedGenerator.getFeedForId(1, "Cat-1").getEvents().size());
        assertEquals(5, feedGenerator.getFeedForId(9, "Cat-1").getEvents().size());
        assertEquals(3, feedGenerator.getRecentFeed("Cat-1").getEvents().size());
        addChunkingHistory(9, 49);
        FeedGenerator newFeedGenerator = new NumberFeedGenerator(allEventRecords, allEventRecordsOffsetMarkers, chunkingEntries);
        assertEquals(3, newFeedGenerator.getRecentFeed("Cat-1").getEvents().size());
        assertEquals(3, newFeedGenerator.getFeedForId(10, "Cat-1").getEvents().size());
        generateData(49, "Cat-1");
        markerService.markEvents(new String[]{"Cat-1", ""}, 50);
        assertEquals(3, newFeedGenerator.getFeedForId(10, "Cat-1").getEvents().size());
        assertEquals(9, newFeedGenerator.getFeedForId(11, "Cat-1").getEvents().size());
        assertEquals(4, newFeedGenerator.getFeedForId(16, "Cat-1").getEvents().size());
        markerService.markEvents(new String[]{"Cat-1", ""}, 97);
        assertEquals(3, newFeedGenerator.getFeedForId(10, "Cat-1").getEvents().size());
        assertEquals(9, newFeedGenerator.getFeedForId(11, "Cat-1").getEvents().size());
        assertEquals(4, newFeedGenerator.getFeedForId(16, "Cat-1").getEvents().size());
    }

    private void addChunkingHistory(final Integer chunkSize, final Integer startId) throws SQLException {
        atomfeedTransactionManager.executeWithTransaction(new AFTransactionWorkWithoutResult() {
            @Override
            protected void doInTransaction() {
                try {
                    Statement statement = connectionProvider.getConnection().createStatement();
                    String tableName = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "chunking_history");
                    statement.execute(String.format("insert into %s (chunk_length, start) values (%d, %d)", tableName, chunkSize, startId));
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error occurred while trying to clear records.", e);
                }
            }

            @Override
            public PropagationDefinition getTxPropagationDefinition() {
                return PropagationDefinition.PROPAGATION_REQUIRED;
            }
        });

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
                return PropagationDefinition.PROPAGATION_REQUIRED;
            }
        });

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
