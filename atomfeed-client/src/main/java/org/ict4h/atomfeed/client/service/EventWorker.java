package org.ict4h.atomfeed.client.service;

import org.ict4h.atomfeed.client.domain.Event;

public interface EventWorker {
    void process(Event event);
}
