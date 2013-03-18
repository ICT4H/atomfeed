package org.ict4h.atomfeed.client.api;

import org.ict4h.atomfeed.client.api.data.Event;

import java.net.URI;
import java.util.List;

public interface FeedClient {
    List<Event> unprocessedEvents(URI feedUri);
    void processedTo(URI feedUri, String eventId);
}
