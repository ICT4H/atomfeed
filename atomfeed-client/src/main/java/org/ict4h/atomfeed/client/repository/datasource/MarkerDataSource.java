package org.ict4h.atomfeed.client.repository.datasource;

import org.ict4h.atomfeed.client.domain.Marker;

import java.net.URI;

public interface MarkerDataSource {
    Marker get(URI feedUri);
    void put(Marker marker);
}