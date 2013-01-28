package org.ict4htw.atomfeed.client.repository;

import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.datasource.MarkerDataSource;

import java.net.URI;

public class AllMarkers {
    private MarkerDataSource markerDataSource;

    public AllMarkers(MarkerDataSource markerDataSource) {
        this.markerDataSource = markerDataSource;
    }

    public Marker get(URI workingFeed) {
        return markerDataSource.get(workingFeed);
    }

    public void update(Marker marker, String entryId) {
        Marker to = marker.updateTo(entryId);
        markerDataSource.put(to);
    }
}