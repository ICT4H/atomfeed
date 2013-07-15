package org.ict4h.atomfeed.client.repository;

import org.ict4h.atomfeed.client.domain.Marker;

import java.net.URI;

public interface AllMarkers {
    public Marker get(URI feedUri);
    public void put(URI feedUri, String entryId, URI entryFeedUri);
}