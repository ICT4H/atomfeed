package org.ict4h.atomfeed.client.domain;

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

    public String getId() {
        return event.getId();
    }
}
