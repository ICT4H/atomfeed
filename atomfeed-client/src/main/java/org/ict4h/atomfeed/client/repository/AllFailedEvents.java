package org.ict4h.atomfeed.client.repository;

import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.domain.FailedEventRetryLog;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public interface AllFailedEvents {

    FailedEvent get(String feedUri, String eventId);

    void addOrUpdate(FailedEvent failedEvent);

    List<FailedEvent> getOldestNFailedEvents(String feedUri, int numberOfFailedEvents, int failedEventMaxRetry);

    int getNumberOfFailedEvents(String feedUri);

    void remove(FailedEvent failedEvent);

    void insert(FailedEventRetryLog failedEventRetryLog);

    List<FailedEvent> getFailedEvents(String feedUri);

    FailedEvent getByEventId(String eventId);

    List<FailedEventRetryLog> getFailedEventRetryLogs(String eventId);

}
