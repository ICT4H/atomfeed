package org.ict4h.atomfeed.server.service;

public interface OffsetMarkerService {

    void markEvents(Integer offsetBy);

    void markEvents(String[] categories, Integer offsetBy);

}
