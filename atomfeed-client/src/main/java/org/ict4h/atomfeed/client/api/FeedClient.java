package org.ict4h.atomfeed.client.api;

import org.ict4h.atomfeed.client.api.data.Event;

import java.net.URI;
import java.util.List;

public interface FeedClient {
    void processEvents(URI feedUri, EventWorker eventWorker);

    void processFailedEvents(URI feedUri, EventWorker eventWorker);
}
