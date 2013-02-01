package org.ict4htw.atomfeed.server.service;

import org.joda.time.DateTime;

import java.net.URI;
import java.net.URISyntaxException;

public class Event {

    private String uuid;

    private String title;

    // Not sure if we should have a dependency on another lib here.
    private DateTime timeStamp;

    private URI uri;

    private String serializedContents;


    public Event(String uuid, String title, DateTime timeStamp, URI uri, String serializedContents) {
        this.uuid = uuid;
        this.timeStamp = timeStamp;
        this.uri = uri;
        this.serializedContents = serializedContents;
        this.title = title;
    }

    public Event(String uuid, String title, DateTime timeStamp, String uriString, String serializedContents) throws URISyntaxException {
        this(uuid, title, timeStamp, new URI(uriString), serializedContents);
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

    public String getContents() {
        return serializedContents;
    }

    public String getTitle() {
        return title;
    }
}