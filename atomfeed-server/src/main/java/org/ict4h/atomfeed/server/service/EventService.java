package org.ict4h.atomfeed.server.service;

public interface EventService {
    /**
     * Publishes an {@code Event}. This results in an {@code Event} being created
     * in the underlying data store.
     * @param event an {@code Event}
     * @throws RuntimeException when an {@code Event} cannot be created.
     */
    public void notify(Event event);
}
