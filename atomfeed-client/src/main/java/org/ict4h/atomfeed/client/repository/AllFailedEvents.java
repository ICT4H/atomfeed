package org.ict4h.atomfeed.client.repository;

import org.ict4h.atomfeed.client.domain.FailedEvent;

import java.util.List;

public interface AllFailedEvents {

    public FailedEvent get(String id);

    public void put(FailedEvent failedEvent);

    public List<FailedEvent> getLastNFailedEvents(String feedUri, int numberOfFailedEvents);

    public List<FailedEvent> getAllFailedEvents(String feedUri);

    public int getNumberOfFailedEvents(String feedUri);

    void remove(FailedEvent failedEvent);
}
