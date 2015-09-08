package org.ict4h.atomfeed.server.service.feedgenerator;

import org.apache.commons.lang3.StringUtils;
import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.repository.AllEventRecordsOffsetMarkers;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsOffsetMarkersJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.service.NumberOffsetMarkerServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;

@Ignore
public class BenchmarkTest extends IntegrationTest {

    private JdbcConnectionProvider connectionProvider;
    private AllEventRecordsJdbcImpl allEventRecords;
    private NumberFeedGenerator feedGenerator;
    private NumberOffsetMarkerServiceImpl markerService;
    private AllEventRecordsOffsetMarkers eventRecordsOffsetMarkers;
    private ChunkingEntries chunkingEntries;

    @Before
    public void setUp() throws Exception {
        connectionProvider = getConnectionProvider();
        //addChunkingHistory(5, 1);

        allEventRecords = new AllEventRecordsJdbcImpl(connectionProvider);
        eventRecordsOffsetMarkers = new AllEventRecordsOffsetMarkersJdbcImpl(connectionProvider);
        chunkingEntries = new ChunkingEntriesJdbcImpl(connectionProvider);

        feedGenerator = new NumberFeedGenerator(allEventRecords, eventRecordsOffsetMarkers, chunkingEntries);
        markerService = new NumberOffsetMarkerServiceImpl(allEventRecords, chunkingEntries, eventRecordsOffsetMarkers);
    }



    @Test
    public void shouldGetFeedWithMultipleChunkingHistoriesWithoutMarkers() throws Exception {
//        markerService.markEvents(new String[]{"patient", "Encounter", ""}, 1000);
        System.out.println(System.currentTimeMillis());
        feedGenerator.getFeedForId(1, "patient");
        System.out.println(System.currentTimeMillis());
        feedGenerator.getRecentFeed("patient");
        System.out.println(System.currentTimeMillis());
        feedGenerator.getFeedForId(53250, "patient");
        System.out.println(System.currentTimeMillis());
    }

    private void addChunkingHistory(Integer chunkSize, Integer startId) throws SQLException {
        Statement statement = connectionProvider.getConnection().createStatement();
        String tableName = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "chunking_history");
        statement.execute(String.format("insert into %s (chunk_length, start) values (%d, %d)", tableName, chunkSize, startId));
        connectionProvider.getConnection().commit();
        statement.close();
    }

    private void clearChunkingHistory() throws SQLException {
        Statement statement = connectionProvider.getConnection().createStatement();
        String tableName = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "chunking_history");
        statement.execute(String.format("delete from %s", tableName));
        connectionProvider.getConnection().commit();
        statement.close();

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
