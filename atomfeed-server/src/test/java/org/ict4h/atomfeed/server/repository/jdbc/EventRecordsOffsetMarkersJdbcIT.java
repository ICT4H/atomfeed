package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.EventRecordsOffsetMarker;
import org.ict4h.atomfeed.server.repository.AllEventRecordsOffsetMarkers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class EventRecordsOffsetMarkersJdbcIT extends IntegrationTest {

    private AllEventRecordsOffsetMarkers allEventRecordsOffsetMarkers;
    private JdbcConnectionProvider connectionProvider;

    @Before
    public void before() throws SQLException {
        connectionProvider = getConnectionProvider();
        allEventRecordsOffsetMarkers = new AllEventRecordsOffsetMarkersJdbcImpl(getConnectionProvider());
        clearRecords();
    }

    @After
    public void after() throws SQLException {
        clearRecords();
        connectionProvider.closeConnection(connectionProvider.getConnection());
    }

    private void clearRecords() throws SQLException {
        Statement statement = connectionProvider.getConnection().createStatement();
        String tableName = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "event_records_offset_marker");
        statement.execute(String.format("delete from %s", tableName));
        connectionProvider.getConnection().commit();
        statement.close();
    }

    @Test
    public void shouldAddEventRecordAndFetchByUUID() throws URISyntaxException, SQLException {
        allEventRecordsOffsetMarkers.addOrUpdate("CAT-01", 10, 5);
        allEventRecordsOffsetMarkers.addOrUpdate("CAT-02", 10, 4);

        List<EventRecordsOffsetMarker> markers = allEventRecordsOffsetMarkers.getAll();
        assertEquals(2, markers.size());
        assertEquals("CAT-01", markers.get(0).getCategory());
        assertEquals("CAT-02", markers.get(1).getCategory());
    }

}
