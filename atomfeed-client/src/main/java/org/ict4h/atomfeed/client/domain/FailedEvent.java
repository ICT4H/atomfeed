package org.ict4h.atomfeed.client.domain;

import java.util.Date;

public class FailedEvent {
    private long failedAt;
    private Event event;
    private String errorMessage;
    private String feedUri;
    private int retries;

    public FailedEvent(String feedUri, Event event, String errorMessage, int retries) {
        if (feedUri == null || feedUri.trim().equals("") || event == null)
            throw new IllegalArgumentException("Arguments provided should not be null.");
        this.event = event;
        this.errorMessage = errorMessage;
        this.feedUri = feedUri;
        this.failedAt = new Date().getTime();
        this.retries = retries;
    }

    public FailedEvent(String feedUri, Event event, String errorMessage, long failedAt, int retries) {
        this(feedUri, event, errorMessage, retries);
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

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void incrementRetryCount() {
        this.retries++;
    }

    @Override
    public String toString() {
        return String.format("FailedEvent{failedAt=%d, event=%s, errorMessage='%s', feedUri='%s'}", failedAt, event, errorMessage, feedUri);
    }

}
