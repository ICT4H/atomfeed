package org.ict4htw.atomfeed.spring.resource;

public class AtomResponse {
    private ResponseStatus status;

    public AtomResponse(ResponseStatus status) {

        this.status = status;
    }

    public static enum ResponseStatus{
        SUCCESS, FAILURE
    }
}
