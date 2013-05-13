package org.ict4h.atomfeed.client.repository;

import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.datasource.MarkerDataSource;

import java.net.URI;

public class AllMarkers {
    private MarkerDataSource markerDataSource;

    public AllMarkers(MarkerDataSource markerDataSource) {
        this.markerDataSource = markerDataSource;
    }

    public Marker get(URI feedUri) {
        return markerDataSource.get(feedUri);
    }

    public void put(URI feedUri, String entryId, URI entryFeedUri) {
        markerDataSource.put(new Marker(feedUri, entryId, entryFeedUri));
    }
}