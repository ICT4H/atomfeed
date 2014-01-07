package org.ict4h.atomfeed.server.service.feedgenerator;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.EventRecordsOffsetMarkersJdbcImpl;
import org.ict4h.atomfeed.server.service.NumberOffsetMarkerServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Statement;

@Ignore
public class NumberOffsetMarkerServiceImplIT extends IntegrationTest {
    private JdbcConnectionProvider connectionProvider;
    private NumberOffsetMarkerServiceImpl markerService;

    @Before
    public void before() throws SQLException {
        clearRecords();
        connectionProvider = getConnectionProvider();
        AllEventRecords allEventRecords = new AllEventRecordsJdbcImpl(connectionProvider);
        ChunkingEntries chunkingEntries = new ChunkingEntriesJdbcImpl(connectionProvider);
        markerService = new NumberOffsetMarkerServiceImpl(allEventRecords, chunkingEntries, new EventRecordsOffsetMarkersJdbcImpl(connectionProvider));
    }

    @After
    public void after() throws SQLException {
        clearRecords();
        connectionProvider.closeConnection(connectionProvider.getConnection());
    }

    @Test
    public void shouldSetOffSetMarkerForCategories() {
        String[] categories = {"Cat-0", "Cat-1", ""};
        markerService.markEvents(categories, 1000);
    }

    private void clearRecords() throws SQLException {
        Statement statement = connectionProvider.getConnection().createStatement();
        String tableName = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "event_records");
        Integer offsetId = 10690;
        statement.execute(String.format("delete from %s where id > %d", tableName, offsetId));
        connectionProvider.getConnection().commit();
        statement.close();
    }

}
