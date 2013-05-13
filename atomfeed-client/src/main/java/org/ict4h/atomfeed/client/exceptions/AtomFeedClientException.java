package org.ict4h.atomfeed.client.exceptions;

public class AtomFeedClientException extends RuntimeException{
    public AtomFeedClientException() {
    }

    public AtomFeedClientException(String message) {
        super(message);
    }

    public AtomFeedClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public AtomFeedClientException(Throwable cause) {
        super(cause);
    }

    public AtomFeedClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
