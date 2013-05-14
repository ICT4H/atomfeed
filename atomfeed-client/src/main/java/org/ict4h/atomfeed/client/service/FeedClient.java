package org.ict4h.atomfeed.client.service;

import java.net.URI;

public interface FeedClient {
    void processEvents(URI feedUri, EventWorker eventWorker);

    void processFailedEvents(URI feedUri, EventWorker eventWorker);
}
