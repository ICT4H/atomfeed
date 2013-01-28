package org.ict4htw.atomfeed.client.repository;

import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.datasource.MarkerDataSource;

import java.net.URI;

public class AllMarkers {
    private MarkerDataSource markerDataSource;

    public AllMarkers(MarkerDataSource markerDataSource) {
        this.markerDataSource = markerDataSource;
    }

    public Marker get(URI feedUri) {
        return markerDataSource.get(feedUri);
    }

    public void processedTo(URI feedUri, String entryId) {
        markerDataSource.put(new Marker(feedUri, entryId));
    }
}