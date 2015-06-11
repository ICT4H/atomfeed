package org.ict4h.atomfeed.client.domain;

public class FailedEventRetryLog {
    private String feedUri;
    private long failedAt;
    private String errorMessage;
    private String eventId;
    private String eventContent;

    public FailedEventRetryLog(String feedUri, long failedAt, String errorMessage, String eventId, String eventContent) {
        this.feedUri = feedUri;
        this.failedAt = failedAt;
        this.errorMessage = errorMessage;
        this.eventId = eventId;
        this.eventContent = eventContent;
    }

    public String getFeedUri() {
        return feedUri;
    }

    public long getFailedAt() {
        return failedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventContent() {
        return eventContent;
    }

    @Override
    public String toString() {
        return String.format("FailedEventRetryLog{feedUri='%s', failedAt=%d, errorMessage='%s', eventId='%s', eventContent='%s'}", feedUri, failedAt, errorMessage, eventId, eventContent);
    }
}
