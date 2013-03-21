package org.ict4h.atomfeed.client.repository.datasource;

import org.ict4h.atomfeed.client.domain.Marker;

import java.net.URI;
import java.util.HashMap;

public class MemoryMarkerDataSource implements MarkerDataSource{

    private final HashMap<URI,Marker> map;

    public MemoryMarkerDataSource() {
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
