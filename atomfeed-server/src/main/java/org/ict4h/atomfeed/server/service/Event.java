package org.ict4h.atomfeed.server.service;


import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

public class Event {

    private String uuid;

    private String title;

    private LocalDateTime timeStamp;

    private URI uri;

    private String serializedContents;

    private String category;

    private String tags;


    public Event(String uuid, String title, LocalDateTime timeStamp, URI uri, String serializedContents, String category) {
        this.uuid = uuid;
        this.timeStamp = timeStamp;
        this.uri = uri;
        this.serializedContents = serializedContents;
        this.title = title;
        this.category = category;
    }

    public Event(String uuid, String title, LocalDateTime timeStamp, String uriString, String serializedContents, String category) throws URISyntaxException {
        this(uuid, title, timeStamp, new URI(uriString), serializedContents, category);
    }

    public Event(String uuid, String title, LocalDateTime timeStamp, URI uri, String serializedContents, String category, String tags) {
        this(uuid, title, timeStamp, uri, serializedContents, category);
        this.tags = tags;
    }

    public String getUuid() {
        return uuid;
    }

    public LocalDateTime getTimeStamp() {
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

    public String getCategory() {
        return category;
    }

    public String getTags() {
        return tags;
    }
}