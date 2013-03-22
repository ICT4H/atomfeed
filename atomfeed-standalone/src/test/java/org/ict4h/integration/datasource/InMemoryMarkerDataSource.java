package org.ict4h.integration.datasource;

import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.datasource.MarkerDataSource;

import java.net.URI;
import java.util.HashMap;

public class InMemoryMarkerDataSource implements MarkerDataSource {

    private final HashMap<URI,Marker> map;

    public InMemoryMarkerDataSource() {
        map = new HashMap<URI,Marker>();
    }

    @Override
    public Marker get(URI feedUri) {
        return map.get(feedUri);
    }

    @Override
    public void put(Marker marker) {
        map.put(marker.getFeedUri(),marker);
    }
}
