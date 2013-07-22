package org.ict4h.atomfeed.client.service;

public interface FeedClient {

    void processEvents();

    void processFailedEvents();
}
