package org.ict4h.atomfeed.client.repository.jdbc;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.client.domain.Marker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AllMarkersJdbcImplIT extends IntegrationTest {

    private AllMarkersJdbcImpl allMarkers;
    private Connection connection;

    private void clearRecords() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("delete from atomfeed.markers");
        statement.close();
    }

    @Before
    public void setUp() throws SQLException {
        connection = getConnection();
        allMarkers = new AllMarkersJdbcImpl(getProvider(connection));
    }

    @After
    public void tearDown() throws SQLException {
        clearRecords();
        connection.close();
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
