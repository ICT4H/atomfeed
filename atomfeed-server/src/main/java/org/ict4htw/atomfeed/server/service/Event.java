package org.ict4htw.atomfeed.server.service;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public class Event implements Serializable {

    private String uuid;

    private String title;

    // Not sure if we should have a dependency on another lib here.
    private DateTime timeStamp;

    private URI uri;

    private Object eventObject;


    public Event(String uuid, String title, DateTime timeStamp, URI uri, Object eventObject) {
        this.uuid = uuid;
        this.timeStamp = timeStamp;
        this.uri = uri;
        this.eventObject = eventObject;
        this.title = title;
    }

    public Event(String uuid, String title, DateTime timeStamp, String uriString, Object eventObject) throws URISyntaxException {
        this.uuid = uuid;
        this.timeStamp = timeStamp;
        this.title = title;
        this.uri = new URI(uriString);
        this.eventObject = eventObject;
    }

    public String getUuid() {
        return uuid;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public URI getUri() {
        return uri;
    }

    public Object getEventObject() {
        return eventObject;
    }

    public String getTitle() {
        return title;
    }
}