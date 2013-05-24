package org.ict4h.atomfeed.client.domain;

import java.sql.Timestamp;
import java.util.Date;

public class FailedEvent {

    private long failedAt;
    private Event event;
    private String errorMessage;
    private String feedUri;

    public FailedEvent(String feedUri, Event event, String errorMessage) {
        if (feedUri == null || feedUri.trim().equals("") || event == null)
            throw new IllegalArgumentException("Arguments provided should not be null.");

        this.event = event;
        this.errorMessage = errorMessage;
        this.feedUri = feedUri;
        this.failedAt = new Date().getTime();
    }

    public FailedEvent(String feedUri, Event event, String errorMessage, long failedAt) {
        this(feedUri, event, errorMessage);
        this.failedAt = failedAt;
    }

    public long getFailedAt() {
        return failedAt;
    }

    public Event getEvent() {
        return event;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFeedUri() {
        return feedUri;
    }

    public String getEventId() {
        return event.getId();
    }

    public void setFailedAt(long failedAt) {
        this.failedAt = failedAt;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
