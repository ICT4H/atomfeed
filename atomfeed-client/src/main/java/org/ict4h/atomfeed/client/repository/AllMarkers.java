package org.ict4h.atomfeed.client.repository;

import org.ict4h.atomfeed.client.domain.Marker;

import java.net.URI;
import java.util.List;

public interface AllMarkers {
    Marker get(URI feedUri);
    void put(URI feedUri, String entryId, URI entryFeedUri);
    List<Marker> getMarkerList();
}