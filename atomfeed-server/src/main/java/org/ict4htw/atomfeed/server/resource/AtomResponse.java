package org.ict4htw.atomfeed.server.resource;

/**
 * Created with IntelliJ IDEA.
 * User: karthik
 * Date: 07/01/13
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AtomResponse {
    private ResponseStatus status;

    public AtomResponse(ResponseStatus status) {

        this.status = status;
    }

    public static enum ResponseStatus{
        SUCCESS, FAILURE
    }
}
