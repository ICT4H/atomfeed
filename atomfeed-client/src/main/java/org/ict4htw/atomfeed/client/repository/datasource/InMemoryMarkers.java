package org.ict4htw.atomfeed.client.repository.datasource;

import org.ict4htw.atomfeed.client.domain.Marker;
import java.net.URI;
import java.util.HashMap;

public class InMemoryMarkers implements MarkerDataSource {
    private HashMap<URI, Marker> markers = new HashMap<URI, Marker>();

    @Override
    public Marker get(URI feedUri) {
        return markers.get(feedUri);
    }

    @Override
    public void put(Marker marker) {
        markers.put(marker.getFeedUri(), marker);
    }
}