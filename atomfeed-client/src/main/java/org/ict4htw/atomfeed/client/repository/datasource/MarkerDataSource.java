package org.ict4htw.atomfeed.client.repository.datasource;

import org.ict4htw.atomfeed.client.domain.Marker;

public interface MarkerDataSource {
    Marker get(String consumerId);
    void update(Marker marker);
}