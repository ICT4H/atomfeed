package org.ict4h.atomfeed.server.exceptions;

public class AtomFeedRuntimeException extends RuntimeException {
    public AtomFeedRuntimeException(String message) {
        super(message);
    }

    public AtomFeedRuntimeException(Throwable t) {
        super(t);
    }
}
