package org.ict4h.atomfeed.client;

public class AtomFeedClientException extends RuntimeException {
    public AtomFeedClientException(Throwable cause) {
        super(cause);
    }

    public AtomFeedClientException(String message) {
        super(message);
    }
}