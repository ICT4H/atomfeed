package org.ict4h.atomfeed.client.repository.jdbc;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AllMarkersJdbcImplIT extends IntegrationTest {

    private AllMarkersJdbcImpl allMarkers;
    private JdbcConnectionProvider connectionProvider;

    private void clearRecords() throws SQLException {
        Statement statement = connectionProvider.getConnection().createStatement();
        String tableName = JdbcUtils.getTableName(getProperty("atomdb.default_schema"), "markers");
        statement.execute("delete from " + tableName);
        statement.close();
    }

    @Before
    public void setUp() throws SQLException {
        connectionProvider = getConnectionProvider();
        allMarkers = new AllMarkersJdbcImpl(connectionProvider);
    }

    @After
    public void tearDown() throws SQLException {
        clearRecords();
        connectionProvider.closeConnection(connectionProvider.getConnection());
    }

    @Test
    public void shouldAddMarkerToDBAndFetchIt() throws URISyntaxException {
        URI feedUri = new URI("http://feedUri");
        String lastReadEntryId = "lastReadEntryId";
        URI entryFeedUri = new URI("http://feedURIForLastReadEntry");

        Marker marker = allMarkers.get(feedUri);
        assertNull(marker);

        allMarkers.put(feedUri, lastReadEntryId, entryFeedUri);
        marker = allMarkers.get(feedUri);

        assertEquals(feedUri, marker.getFeedUri());
        assertEquals(lastReadEntryId, marker.getLastReadEntryId());
        assertEquals(entryFeedUri, marker.getFeedURIForLastReadEntry());
    }

    @Test
    public void shouldUpdateMarkerWhenExists() throws URISyntaxException {
        URI feedUri = new URI("http://feedUri");
        String lastReadEntryId = "lastReadEntryId";
        URI entryFeedUri = new URI("http://feedURIForLastReadEntry");

        allMarkers.put(feedUri, "1", new URI("http://initialFeedUri"));
        allMarkers.put(feedUri, lastReadEntryId, entryFeedUri);
        Marker marker = allMarkers.get(feedUri);

        assertEquals(feedUri, marker.getFeedUri());
        assertEquals(lastReadEntryId, marker.getLastReadEntryId());
        assertEquals(entryFeedUri, marker.getFeedURIForLastReadEntry());
    }
}
