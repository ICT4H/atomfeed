package org.ict4htw.atomfeed.client.repository;

import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.datasource.MarkerDataSource;

public class AllMarkers {
    private MarkerDataSource markerDataSource;

    public AllMarkers(MarkerDataSource markerDataSource) {
        this.markerDataSource = markerDataSource;
    }

    public Marker get(String consumerId) {
        return markerDataSource.get(consumerId);
    }

    public void update(String consumerId, String feedEntryId) {
        Marker marker = get(consumerId);
        if (marker == null) {
        	marker = new Marker(consumerId, feedEntryId);
        } else {
        	marker.setFeedEntryId(feedEntryId);
        }
        markerDataSource.update(marker);
    }
}