package org.ict4h.atomfeed.client.repository;

import org.ict4h.atomfeed.client.domain.FailedEvent;

import java.util.List;

public interface AllFailedEvents {

    public FailedEvent get(String feedUri, String eventId);

    public void addOrUpdate(FailedEvent failedEvent);

    public List<FailedEvent> getOldestNFailedEvents(String feedUri, int numberOfFailedEvents);

    public int getNumberOfFailedEvents(String feedUri);

    void remove(FailedEvent failedEvent);
}
