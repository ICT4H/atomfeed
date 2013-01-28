package org.ict4htw.atomfeed.client.api;

import org.ict4htw.atomfeed.client.api.data.Event;

import java.net.URI;
import java.util.List;

public interface FeedClient {
    List<Event> unprocessedEvents(URI feedUri);
    void processedTo(URI feedUri, String entryId);
}
