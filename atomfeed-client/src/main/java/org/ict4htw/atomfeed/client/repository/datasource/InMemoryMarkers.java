package org.ict4htw.atomfeed.client.repository.datasource;

import org.ict4htw.atomfeed.client.domain.Marker;

import java.util.HashMap;

public class InMemoryMarkers implements MarkerDataSource {
    private HashMap<String, Marker> markers = new HashMap<String, Marker>();

    @Override
    public Marker get(String consumerId) {
        return markers.get(consumerId);
    }

    @Override
    public void update(String consumerId, Marker marker) {
        markers.put(consumerId, marker);
    }

    public void add(String consumerId, Marker marker) {
        markers.put(consumerId, marker);
    }
}