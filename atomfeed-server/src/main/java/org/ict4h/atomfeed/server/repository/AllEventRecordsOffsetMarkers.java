package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.server.domain.EventRecordsOffsetMarker;

import java.util.List;

public interface AllEventRecordsOffsetMarkers {
    void addOrUpdate(String category, Integer offsetId, Integer countTillOffSetId);
    List<EventRecordsOffsetMarker> getAll();
}
