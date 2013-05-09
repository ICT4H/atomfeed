package org.ict4h.atomfeed.client.api;

import org.ict4h.atomfeed.client.api.data.Event;

public interface EventWorker {
    void process(Event event);
}
