package org.ict4htw.atomfeed.client.repository;

import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.datasource.MarkerDataSource;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AllMarkersTest {

    AllMarkers allMarkers;
    private MarkerDataSource markerDataSource;

    @Before
    public void setup() throws URISyntaxException {
        markerDataSource=getMarketDatasource();
        allMarkers=new AllMarkers(markerDataSource);
    }

    private MarkerDataSource getMarketDatasource() throws URISyntaxException {
        MarkerDataSource mock = mock(MarkerDataSource.class);
        URI feedUri = new URI("http://testfeeduri");
        Marker testEntryId = new Marker(feedUri, "testEntryId");
        when(mock.get(feedUri)).thenReturn(testEntryId);
        return mock;
    }

    @Test
    public void testGet() throws Exception {
        URI feedUri = new URI("http://testfeeduri");
        Marker marker = allMarkers.get(feedUri);
        verify(markerDataSource).get(feedUri);
        assertEquals(marker, new Marker(feedUri, "testEntryId"));
    }

    @Test
    public void testProcessedTo() throws Exception {
        URI testFeedUri = new URI("http://testfeeduri");
        String testEntryId = "testEntryId";
        allMarkers.processedTo(testFeedUri, testEntryId);
        verify(markerDataSource).put(new Marker(testFeedUri, testEntryId));
    }
}
