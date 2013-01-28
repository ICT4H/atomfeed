package org.ict4htw.atomfeed.client.repository.datasource;

import org.ict4htw.atomfeed.client.domain.Marker;

import java.net.URI;

public interface MarkerDataSource {
    Marker get(URI feedUri);
    void put(Marker marker);
}