package org.ict4h.atomfeed.client.repository;

import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.datasource.MarkerDataSource;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AllMarkersTest {

    AllMarkers allMarkers;
    private MarkerDataSource markerDataSource;

    private URI feedUri;
    private URI lastReadFeedUri;
    private String testEntryId;

    @Before
    public void setup() throws URISyntaxException {
        feedUri = new URI("http://testFeedUri");
        lastReadFeedUri = new URI("http://lastReadFeedUri");
        testEntryId = "testEntryId";

        markerDataSource = getMarketDataSource();
        allMarkers = new AllMarkers(markerDataSource);
    }

    private MarkerDataSource getMarketDataSource() throws URISyntaxException {
        MarkerDataSource mock = mock(MarkerDataSource.class);
        Marker testEntry = new Marker(feedUri, testEntryId, lastReadFeedUri);
        when(mock.get(feedUri)).thenReturn(testEntry);
        return mock;
    }

    @Test
    public void testGet() throws Exception {
        Marker marker = allMarkers.get(feedUri);

        verify(markerDataSource).get(feedUri);
        assertEquals(marker, new Marker(feedUri, testEntryId, lastReadFeedUri));
    }

    @Test
    public void testProcessedTo() throws Exception {
        allMarkers.processedTo(feedUri, testEntryId, lastReadFeedUri);

        verify(markerDataSource).put(new Marker(feedUri, testEntryId, lastReadFeedUri));
    }
}
