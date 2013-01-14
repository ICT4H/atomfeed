package org.ict4htw.atomfeed.client.api;

import org.ict4htw.atomfeed.client.api.data.Event;

import java.util.List;

public interface FeedClient {
    List<Event> unprocessedEvents(String consumerId, String url);

    void confirmProcessed(String feedEntryId, String consumerId);
}
